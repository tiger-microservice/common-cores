package com.tiger.cores.aops.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // Chỉ áp dụng cho các field
@Retention(RetentionPolicy.RUNTIME) // Annotation sẽ tồn tại trong runtime
public @interface MaskData {
}