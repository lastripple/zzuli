package com.troublemaker.order.config;

import com.troublemaker.order.entity.FieldInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.order.config
 * @Author: troublemaker
 * @CreateTime: 2022-08-22  18:13
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "data.field")
public class FieldInfoConfig {
   private List<FieldInfo> fields;

   @Bean
   public List<FieldInfo> getFields() {
      return fields;
   }

   public void setFields(List<FieldInfo> fields) {
      this.fields = fields;
   }
}

