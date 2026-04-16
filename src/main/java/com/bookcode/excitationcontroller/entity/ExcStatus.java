package com.bookcode.excitationcontroller.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "exc_status")
@Data
public class ExcStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_no", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'EXC5000'")
    private String deviceNo;
    
    @Column(name = "IDL", columnDefinition = "TINYINT COMMENT '机组分闸 0=否 1=是'")
    private Integer idl;
    
    @Column(name = "IQL", columnDefinition = "TINYINT COMMENT '启励指令 0=否 1=是'")
    private Integer iql;
    
    @Column(name = "YWARN", columnDefinition = "TINYINT COMMENT '故障报警 0=正常 1=故障'")
    private Integer ywarn;
    
    @Column(name = "YERR", columnDefinition = "TINYINT COMMENT '事故跳闸 0=正常 1=跳闸'")
    private Integer yerr;
    
    @Column(name = "YPSS", columnDefinition = "TINYINT COMMENT 'PSS投入 0=退出 1=投入'")
    private Integer ypss;
    
    @Column(name = "MQLF", columnDefinition = "TINYINT COMMENT '启励状态 0=未启励 1=启励中'")
    private Integer mqlf;
    
    @Column(name = "OVER_UG", columnDefinition = "TINYINT COMMENT '过压故障 0=正常 1=故障'")
    private Integer overUg;
    
    @Column(name = "OVER_IG", columnDefinition = "TINYINT COMMENT '过流故障 0=正常 1=故障'")
    private Integer overIg;
    
    @Column(name = "PT_BREAK", columnDefinition = "TINYINT COMMENT 'PT断线 0=正常 1=故障'")
    private Integer ptBreak;
    
    @Column(name = "update_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;
}