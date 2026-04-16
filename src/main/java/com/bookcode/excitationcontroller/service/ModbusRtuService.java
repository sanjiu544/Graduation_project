package com.bookcode.excitationcontroller.service;

import com.bookcode.excitationcontroller.config.ModbusConfig;
import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ModbusRtuService {

    @Autowired
    private ModbusConfig modbusConfig;

    private ModbusSerialMaster master;

    @PostConstruct
    public void init() {
        try {
            connect();
            log.info("Modbus RTU连接成功，串口: {}", modbusConfig.getSerialPort());
        } catch (Exception e) {
            log.error("Modbus RTU初始化失败: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        disconnect();
        log.info("Modbus RTU连接已关闭");
    }

    public void connect() throws Exception {

        try {
            SerialParameters params = new SerialParameters();
            params.setPortName(modbusConfig.getSerialPort());
            params.setBaudRate(modbusConfig.getBaudRate());
            params.setDatabits(modbusConfig.getDataBits());
            params.setStopbits(modbusConfig.getStopBits());
            params.setParity(getParityValue(modbusConfig.getParity()));
            // 强制使用 RTU 编码，避免默认编码与模拟从站不一致导致读超时/读失败。
            params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
            params.setEcho(false);

            log.info("[连接初始化] 尝试连接Modbus RTU: 串口={}, 波特率={}, 数据位={}, 停止位={}, 校验={}",
                     modbusConfig.getSerialPort(), modbusConfig.getBaudRate(),
                     modbusConfig.getDataBits(), modbusConfig.getStopBits(), modbusConfig.getParity());
            master = new ModbusSerialMaster(params);
            master.setTimeout(modbusConfig.getTimeout());
            master.setRetries(modbusConfig.getRetries());
            master.connect();
            log.info("[连接初始化] Modbus RTU连接成功");
        } catch (Exception e) {
            log.error("[连接初始化] Modbus RTU连接失败: {}", e.getMessage());
            throw new Exception("Modbus RTU连接失败: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        if (master != null) {
            master.disconnect();
            master = null;
        }
    }

    public boolean isConnected() {
        return master != null;
    }

    /**
     * 验证连接是否有效
     */
    public boolean isValidConnection() {
        return master != null;
    }

    /**
     * 尝试重新连接
     */
    public boolean reconnect() {
        try {
            disconnect();
            connect();
            log.info("Modbus RTU重新连接成功");
            return true;
        } catch (Exception e) {
            log.error("Modbus RTU重新连接失败: {}", e.getMessage());
            return false;
        }
    }

    public Register[] readHoldingRegisters(int slaveId, int startOffset, int numberOfRegisters) 
            throws Exception {
        if (!isConnected()) {
            log.error("[后端错误] Modbus未连接，无法发送指令");
            throw new Exception("Modbus未连接");
        }
        
        log.info("[通讯开始] 发送Modbus指令: 从站ID={}, 起始地址={}, 寄存器数量={}", slaveId, startOffset, numberOfRegisters);
        
        try {
            long startTime = System.currentTimeMillis();
            Register[] registers = master.readMultipleRegisters(slaveId, startOffset, numberOfRegisters);
            long endTime = System.currentTimeMillis();
            
            if (registers != null) {
                log.info("[通讯成功] 收到响应: 寄存器数量={}, 响应时间={}ms", registers.length, endTime - startTime);
                log.info("[原始数据帧] 从站ID={}, 起始地址={}, 数据:", slaveId, startOffset);
                for (int i = 0; i < registers.length; i++) {
                    int value = registers[i].getValue();
                    log.info("  寄存器[{}]: 0x{:04X} (十进制: {})", i + startOffset, value);
                }
            } else {
                log.warn("[硬件错误] 收到空响应");
            }
            
            return registers;
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Connection") || errorMessage.contains("I/O exception")) {
                log.error("[后端错误] 连接失败: {}", errorMessage);
                // 尝试重新连接
                if (reconnect()) {
                    // 重新尝试读取
                    try {
                        log.info("[重新尝试] 重新发送Modbus指令");
                        Register[] registers = master.readMultipleRegisters(slaveId, startOffset, numberOfRegisters);
                        log.info("[重新尝试] 读取成功");
                        return registers;
                    } catch (Exception re) {
                        log.error("[后端错误] 重新尝试失败: {}", re.getMessage());
                        throw re;
                    }
                }
            } else if (errorMessage.contains("Timeout")) {
                log.error("[硬件错误] 通讯超时: {}", errorMessage);
            } else if (errorMessage.contains("Exception")) {
                log.error("[硬件错误] 从站异常: {}", errorMessage);
            } else {
                log.error("[后端错误] 未知错误: {}", errorMessage);
            }
            throw e;
        }
    }

    public Float readFloat(int slaveId, int offset) throws Exception {
        log.info("[读取操作] 读取浮点数: 从站ID={}, 起始地址={}", slaveId, offset);
        Register[] registers = readHoldingRegisters(slaveId, offset, 2);
        Float value = parseFloat(registers);
        log.info("[读取结果] 浮点数结果: {}", value);
        return value;
    }

    public Integer readInt16(int slaveId, int offset) throws Exception {
        log.info("[读取操作] 读取16位整数: 从站ID={}, 起始地址={}", slaveId, offset);
        Register[] registers = readHoldingRegisters(slaveId, offset, 1);
        Integer value = registers[0].getValue();
        log.info("[读取结果] 16位整数结果: {}", value);
        return value;
    }


    private int getParityValue(String parity) {
        if (parity == null) {
            return 0; /* PARITY_NONE */
        }
        return switch (parity.toUpperCase()) {
            case "NONE", "N" -> 0; // PARITY_NONE
            case "EVEN" -> 2; // PARITY_EVEN
            case "ODD" -> 1; // PARITY_ODD
            case "MARK" -> 3; // PARITY_MARK
            case "SPACE" -> 4; // PARITY_SPACE
            default -> 0; // PARITY_NONE
        };
    }

    public Float parseFloat(Register[] registers) {
        if (registers.length < 2) return null;
    // 大端序：高位字节在前，低位字节在后
    int value = (registers[0].getValue() & 0xFFFF) << 16 | (registers[1].getValue() & 0xFFFF);
    return Float.intBitsToFloat(value);
    }

    /**
     * 读取整数并使用定点缩放法转换为浮点数
     * @param slaveId 从站ID
     * @param offset 寄存器地址
     * @param scale 缩放因子（除数）
     * @return 转换后的浮点数
     * @throws Exception 异常
     */
    public Float readScaledValue(int slaveId, int offset, float scale) throws Exception {
        log.info("[读取操作] 读取定点缩放值: 从站ID={}, 起始地址={}, 缩放因子={}", slaveId, offset, scale);
        Integer rawValue = readInt16(slaveId, offset);
        if (rawValue == null) return null;
        Float scaledValue = rawValue / scale;
        log.info("[读取结果] 原始值: {}, 缩放后: {}", rawValue, scaledValue);
        return scaledValue;
    }

    /**
     * 批量读取多个寄存器并使用定点缩放法转换
     * @param slaveId 从站ID
     * @param startOffset 起始地址
     * @param count 寄存器数量
     * @param scale 缩放因子（除数）
     * @return 转换后的浮点数数组
     * @throws Exception 异常
     */
    public Float[] readScaledValues(int slaveId, int startOffset, int count, float scale) throws Exception {
        log.info("[读取操作] 批量读取定点缩放值: 从站ID={}, 起始地址={}, 数量={}, 缩放因子={}", 
                  slaveId, startOffset, count, scale);
        Register[] registers = readHoldingRegisters(slaveId, startOffset, count);
        if (registers == null || registers.length != count) return null;
        
        Float[] values = new Float[count];
        for (int i = 0; i < count; i++) {
            int rawValue = registers[i].getValue();
            values[i] = rawValue / scale;
            log.info("[读取结果] 寄存器[{}]原始值: {}, 缩放后: {}", startOffset + i, rawValue, values[i]);
        }
        return values;
    }
}