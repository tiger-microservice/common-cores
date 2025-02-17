package com.tiger.cores.serializers.maskdata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tiger.common.utils.StringMaskerUtil;

import java.io.IOException;

public class MaskDataSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // Logic mask data (ví dụ: thay thế bằng dấu *)
            gen.writeString(StringMaskerUtil.markString(value, value.length() - 4, 0));
        } else {
            gen.writeNull();
        }
    }
}