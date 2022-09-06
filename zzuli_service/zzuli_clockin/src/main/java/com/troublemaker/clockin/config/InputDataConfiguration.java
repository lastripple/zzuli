package com.troublemaker.clockin.config;

import com.troublemaker.clockin.entity.YamlCommonData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.config
 * @Author: troublemaker
 * @CreateTime: 2022-08-23  15:26
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data")
public class InputDataConfiguration {
    private YamlCommonData common;
}

