package com.bookcode.excitationcontroller.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "exc_fault")
@Data
public class ExcFault {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_no", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'EXC5000'")
    private String deviceNo;
    
    @Column(name = "fault_name", length = 50, columnDefinition = "VARCHAR(50) COMMENT '故障名称:过压/过流/PT断线'")
    private String faultName;
    
    @Column(name = "fault_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '故障时间'")
    private LocalDateTime faultTime;
    
    @Column(name = "recover_time", columnDefinition = "DATETIME NULL COMMENT '恢复时间'")
    private LocalDateTime recoverTime;
}