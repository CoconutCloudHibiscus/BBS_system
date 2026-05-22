package com.bbs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI配置类
 * <p>配置API文档的基本信息</p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建OpenAPI Bean，设置API文档信息
     *
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BBS论坛系统API")
                        .description("BBS论坛系统后端接口文档")
                        .version("1.0.0"));
    }
}
