package com.tiger.cores.serializers.sensitive;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.tiger.cores.aops.annotations.SensitiveData;

public class SensitiveJsonSerializer extends JsonSerializer<String>
        implements ContextualSerializer, ApplicationContextAware {

    private SensitiveStrategy sensitiveStrategy;
    private SensitiveProperties sensitiveProperties;

    @Override
    public void serialize(String s, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        // Xử lý serialize cho trường
        gen.writeString(sensitiveStrategy.maskingFunction().apply(s));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
            throws JsonMappingException {
        // Kiểm tra xem có cần xử lý ẩn thông tin hay không
        if (desensitization()) {
            // Lấy enum nhạy cảm
            SensitiveData sensitiveAnnotation = property.getAnnotation(SensitiveData.class);
            // Nếu có chú thích nhạy cảm, áp dụng quy tắc ẩn thông tin
            if (Objects.nonNull(sensitiveAnnotation)
                    && Objects.equals(String.class, property.getType().getRawClass())) {
                this.sensitiveStrategy = sensitiveAnnotation.strategy();
                return this;
            }
        }
        return provider.findValueSerializer(property.getType(), property);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        sensitiveProperties = applicationContext.getBean(SensitiveProperties.class);
    }

    /**
     * Kiểm tra xem có cần xử lý ẩn thông tin hay không
     *
     * @return
     */
    private boolean desensitization() {
        return true;
    }
}
