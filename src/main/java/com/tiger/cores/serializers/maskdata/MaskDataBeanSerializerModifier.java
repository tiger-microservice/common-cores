//package com.tiger.cores.serializers.maskdata;
//
//import com.fasterxml.jackson.databind.BeanDescription;
//import com.fasterxml.jackson.databind.BeanProperty;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializationConfig;
//import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
//import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
//import com.tiger.cores.aops.annotations.MaskData;
//
//import java.util.List;
//
//public class MaskDataBeanSerializerModifier extends BeanSerializerModifier {
//
//    @Override
//    public List<BeanProperty> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanProperty> beanProperties) {
//        for (BeanPropertyWriter writer : beanProperties) {
//            if (writer.getAnnotation(MaskData.class) != null) {
//                // Áp dụng MaskDataSerializer cho các field có @MaskData
//                writer.assignSerializer(new MaskDataSerializer());
//            }
//        }
//        return beanProperties;
//    }
//}