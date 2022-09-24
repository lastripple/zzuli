package com.troublemaker.order.config;

import com.troublemaker.order.entity.YamlOrderData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.order.config
 * @Author: troublemaker
 * @CreateTime: 2022-09-24  08:15
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data")
public class YamlOrderDataConfiguration {
    private YamlOrderData order;
}

