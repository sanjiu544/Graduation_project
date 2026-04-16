package com.bookcode.excitationcontroller.task;

import com.bookcode.excitationcontroller.config.ModbusConfig;
import com.bookcode.excitationcontroller.entity.ExcFault;
import com.bookcode.excitationcontroller.entity.ExcRealData;
import com.bookcode.excitationcontroller.entity.ExcStatus;
import com.bookcode.excitationcontroller.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class ModbusDataCollectionTask {

    @Autowired
    private ModbusRtuService modbusRtuService;

    @Autowired
    private ModbusDataParser modbusDataParser;

    @Autowired
    private ModbusConfig modbusConfig;

    @Autowired
    private ExcRealDataService excRealDataService;

    @Autowired
    private ExcStatusService excStatusService;

    @Autowired
    private ExcFaultService excFaultService;

    @Scheduled(fixedRate = 5000)
    public void collectRealData() {
        // 1. 先判断连接状态，未连接直接退出
        if (!modbusRtuService.isConnected()) {
            log.error("[后端错误] Modbus未连接，跳过数据采集");
            return;
        }

        try {
            int slaveId = modbusConfig.getSlaveId();

            // ===================== 核心修复 =====================
            // 使用定点缩放法读取数据
            // 假设缩放因子为10，即硬件发送的是实际值的10倍
            float scaleFactor = 10.0f;
            
            // 读取电压、电流、频率数据（使用定点缩放法）
            Float ug = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_UG, scaleFactor);  // 电压：寄存器4
            Float ig = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_IG, scaleFactor);  // 电流：寄存器5
            Float fg = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_FG, scaleFactor);  // 频率：寄存器6

            // 模拟其他数据
            Float pg = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_PG, scaleFactor);
            Float qg = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_QG, scaleFactor);
            Float il1 = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_IL1, scaleFactor);
            Float ugSet = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_UG_SET, scaleFactor);
            Float ctrlDeg = modbusRtuService.readScaledValue(slaveId, ModbusDataParser.REG_CTRL_DEG, scaleFactor);
            Integer runState = modbusRtuService.readInt16(slaveId, ModbusDataParser.REG_RUN_STATE);

            // 解析并保存
            ExcRealData realData = modbusDataParser.parseRealData(ug, ig, fg, pg, qg, il1, ugSet, ctrlDeg, runState);
            excRealDataService.save(realData);
            log.info("[数据采集] 实时数据采集成功 → 电压:{}V, 电流:{}A, 频率:{}Hz", ug, ig, fg);

        } catch (Exception e) {
            log.error("[数据采集] 实时数据采集失败: {}", e.getMessage(), e);
            // 检查是否是连接问题
            if (e.getMessage() != null && (e.getMessage().contains("Modbus未连接") || e.getMessage().contains("Connection") || e.getMessage().contains("I/O exception"))) {
                log.info("[连接检查] 尝试重新连接Modbus...");
                boolean reconnected = modbusRtuService.reconnect();
                if (reconnected) {
                    log.info("[连接检查] 重新连接成功，将在下一次采集周期尝试读取数据");
                } else {
                    log.error("[连接检查] 重新连接失败，请检查硬件连接");
                }
            }
        }
    }

    private Float safeReadScaled(int slaveId, int register, float scale, float defaultValue) {
        try {
            Float value = modbusRtuService.readScaledValue(slaveId, register, scale);
            return value == null ? defaultValue : value;
        } catch (Exception e) {
            log.warn("[数据采集] 读取寄存器{}失败，使用默认值{}: {}", register, defaultValue, e.getMessage());
            return defaultValue;
        }
    }

    private Integer safeReadInt16(int slaveId, int register, int defaultValue) {
        try {
            Integer value = modbusRtuService.readInt16(slaveId, register);
            return value == null ? defaultValue : value;
        } catch (Exception e) {
            log.warn("[数据采集] 读取寄存器{}失败，使用默认值{}: {}", register, defaultValue, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * 每2秒采集状态数据 ✅ 修复逻辑反转
     */
    @Scheduled(fixedRate = 2000)
    public void collectStatusData() {
        // 未连接直接退出
        if (!modbusRtuService.isConnected()) {
            return;
        }

        try {
            int slaveId = modbusConfig.getSlaveId();

            // 读取状态寄存器（根据你的实际需求调整地址）
            Integer xin = modbusRtuService.readInt16(slaveId, ModbusDataParser.REG_XIN);
            Integer yout = modbusRtuService.readInt16(slaveId, ModbusDataParser.REG_YOUT);
            Integer m1 = modbusRtuService.readInt16(slaveId, ModbusDataParser.REG_M1);
            Integer m2 = modbusRtuService.readInt16(slaveId, ModbusDataParser.REG_M2);

            // 保存状态
            ExcStatus status = modbusDataParser.parseStatusData(xin, yout, m1, m2);
            excStatusService.save(status);

            // 故障检测
            String faultType = modbusDataParser.checkFaultType(status);
            if (faultType != null) {
                ExcFault fault = modbusDataParser.parseFaultData(faultType);
                excFaultService.save(fault);
                log.warn("[故障检测] 检测到故障: {}", faultType);
            }

            log.info("[数据采集] 状态数据采集成功: XIN={}, YOUT={}, M1={}, M2={}", xin, yout, m1, m2);

        } catch (Exception e) {
            log.error("[数据采集] 状态数据采集失败: {}", e.getMessage(), e);
            // 检查是否是连接问题
            if (e.getMessage() != null && (e.getMessage().contains("Modbus未连接") || e.getMessage().contains("Connection") || e.getMessage().contains("I/O exception"))) {
                log.info("[连接检查] 尝试重新连接Modbus...");
                boolean reconnected = modbusRtuService.reconnect();
                if (reconnected) {
                    log.info("[连接检查] 重新连接成功，将在下一次采集周期尝试读取数据");
                } else {
                    log.error("[连接检查] 重新连接失败，请检查硬件连接");
                }
            }
        }
    }
}