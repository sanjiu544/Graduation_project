package com.bookcode.excitationcontroller.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "modbus")
@Data
public class ModbusConfig {
    
    private String serialPort = "COM5";
    private int baudRate = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    /**
     * Serial parity: NONE / EVEN / ODD / MARK / SPACE
     */
    private String parity = "NONE";
    private int slaveId = 8;
    private int timeout = 3000;
    private int retries = 3;
}
