package com.bookcode.excitationcontroller.service;

import com.bookcode.excitationcontroller.entity.ExcRealData;
import com.bookcode.excitationcontroller.entity.ExcStatus;
import com.bookcode.excitationcontroller.entity.ExcFault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ModbusDataParser {

    // Modbus寄存器地址定义
    // 状态位寄存器
    public static final int REG_XIN = 0;         // 16个IO输入位
    public static final int REG_YOUT = 1;        // 16个IO输出位
    public static final int REG_M1 = 2;          // 32个位操作区
    public static final int REG_M2 = 3;          // 32个位操作区
    
    // 数据位寄存器
    public static final int REG_UG = 4;          // 机端电压
    public static final int REG_IG = 5;          // 机端电流
    public static final int REG_FG = 6;          // 频率
    public static final int REG_PG = 7;          // 有功
    public static final int REG_QG = 8;          // 无功
    public static final int REG_SG = 9;          // 视在功率
    public static final int REG_COSG = 10;       // 功率因数
    public static final int REG_UW = 11;         // 网压
    public static final int REG_FRW = 12;        // 网率
    public static final int REG_IL1 = 13;        // 励磁电流1
    public static final int REG_IL2 = 14;        // 励磁电流2
    public static final int REG_IL3 = 15;        // 励磁电流3
    
    // 给定值寄存器
    public static final int REG_UG_SET = 59;     // 机端电压给定
    public static final int REG_IL_SET = 60;     // 励磁电流给定
    public static final int REG_CTRL_DEG = 64;   // 控制角
    public static final int REG_RUN_STATE = 65;  // 运行状态
    
    // 状态位解析
    public static final int BIT_IDL = 0;         // 机组分闸 (Xin bit 0)
    public static final int BIT_IQL = 1;         // 启励指令 (Xin bit 1)
    public static final int BIT_YWARN = 0;       // 故障报警 (Yout bit 0)
    public static final int BIT_YERR = 1;        // 事故跳闸 (Yout bit 1)
    public static final int BIT_YPSS = 2;        // PSS投入 (Yout bit 2)
    public static final int BIT_MQLF = 0;        // 启励状态 (M1 bit 0)
    public static final int BIT_OVER_UG = 1;     // 过压故障 (M1 bit 1)
    public static final int BIT_OVER_IG = 2;     // 过流故障 (M1 bit 2)
    public static final int BIT_PT_BREAK = 3;    // PT断线 (M1 bit 3)

    /**
     * 解析实时数据
     */
    public ExcRealData parseRealData(Float ug, Float ig, Float fg, Float pg, Float qg, 
                                     Float il1, Float ugSet, Float ctrlDeg, Integer runState) {
        ExcRealData data = new ExcRealData();
        data.setDeviceNo("EXC5000");
        data.setUg(ug);
        data.setIg(ig);
        data.setFg(fg);
        data.setPg(pg);
        data.setQg(qg);
        data.setIl1(il1);
        data.setUgSet(ugSet);
        data.setCtrlDeg(ctrlDeg);
        data.setRunState(parseRunState(runState));
        data.setCollectTime(LocalDateTime.now());
        return data;
    }

    /**
     * 解析状态位数据
     */
    public ExcStatus parseStatusData(Integer xin, Integer yout, Integer m1, Integer m2) {
        ExcStatus status = new ExcStatus();
        status.setDeviceNo("EXC5000");
        status.setIdl(getBitValue(xin, BIT_IDL));
        status.setIql(getBitValue(xin, BIT_IQL));
        status.setYwarn(getBitValue(yout, BIT_YWARN));
        status.setYerr(getBitValue(yout, BIT_YERR));
        status.setYpss(getBitValue(yout, BIT_YPSS));
        status.setMqlf(getBitValue(m1, BIT_MQLF));
        status.setOverUg(getBitValue(m1, BIT_OVER_UG));
        status.setOverIg(getBitValue(m1, BIT_OVER_IG));
        status.setPtBreak(getBitValue(m1, BIT_PT_BREAK));
        status.setUpdateTime(LocalDateTime.now());
        return status;
    }

    /**
     * 从整数中提取指定位置的位值
     */
    private Integer getBitValue(Integer value, int bitPosition) {
        if (value == null) return 0;
        return (value & (1 << bitPosition)) != 0 ? 1 : 0;
    }

    /**
     * 解析故障数据
     */
    public ExcFault parseFaultData(String faultName) {
        ExcFault fault = new ExcFault();
        fault.setDeviceNo("EXC5000");
        fault.setFaultName(faultName);
        fault.setFaultTime(LocalDateTime.now());
        return fault;
    }

    /**
     * 解析运行状态
     */
    private String parseRunState(Integer state) {
        if (state == null) return "未知";
        switch (state) {
            case 0: return "停机";
            case 1: return "运行";
            case 2: return "故障";
            default: return "未知";
        }
    }

    /**
     * 根据状态值判断故障类型
     */
    public String checkFaultType(ExcStatus status) {
        if (status.getOverUg() != null && status.getOverUg() == 1) {
            return "过压故障";
        }
        if (status.getOverIg() != null && status.getOverIg() == 1) {
            return "过流故障";
        }
        if (status.getPtBreak() != null && status.getPtBreak() == 1) {
            return "PT断线";
        }
        return null;
    }
}
