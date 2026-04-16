package com.bookcode.excitationcontroller.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "exc_real_data")
@Data
public class ExcRealData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_no", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'EXC5000'")
    private String deviceNo;
    
    @Column(name = "ug", columnDefinition = "FLOAT COMMENT '机端电压(V)'")
    private Float ug;
    
    @Column(name = "ig", columnDefinition = "FLOAT COMMENT '机端电流(A)'")
    private Float ig;
    
    @Column(name = "fg", columnDefinition = "FLOAT COMMENT '电网频率(Hz)'")
    private Float fg;
    
    @Column(name = "pg", columnDefinition = "FLOAT COMMENT '有功功率(kW)'")
    private Float pg;
    
    @Column(name = "qg", columnDefinition = "FLOAT COMMENT '无功功率(kvar)'")
    private Float qg;
    
    @Column(name = "il1", columnDefinition = "FLOAT COMMENT '励磁电流(A)'")
    private Float il1;
    
    @Column(name = "ug_set", columnDefinition = "FLOAT COMMENT '电压设定值'")
    private Float ugSet;
    
    @Column(name = "ctrl_deg", columnDefinition = "FLOAT COMMENT '调节角度'")
    private Float ctrlDeg;
    
    @Column(name = "run_state", length = 20, columnDefinition = "VARCHAR(20) COMMENT '运行状态:停机/运行/故障'")
    private String runState;
    
    @Column(name = "collect_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '采集时间'")
    private LocalDateTime collectTime;
}