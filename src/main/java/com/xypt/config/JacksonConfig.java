package com.xypt.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局序列化配置
 *
 * 解决 LocalDateTime 默认输出 "2026-03-07T09:00:00"（含T）的问题，
 * 统一改为 "2026-03-07 09:00:00" 格式，前后端展示一致。
 *
 * 注意：application.yml 中的 spring.jackson.date-format 只对 java.util.Date 生效，
 *       LocalDateTime 需要通过此处单独配置。
 */
@Configuration
public class JacksonConfig {

    /** 全局日期时间格式 */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return builder -> {
            // LocalDateTime 序列化：输出为 "2026-03-07 09:00:00"
            builder.serializerByType(LocalDateTime.class,
                    new LocalDateTimeSerializer(formatter));
            // LocalDateTime 反序列化：接收 "2026-03-07 09:00:00" 格式
            builder.deserializerByType(LocalDateTime.class,
                    new LocalDateTimeDeserializer(formatter));
            // 禁止将日期序列化为时间戳（毫秒数）
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
