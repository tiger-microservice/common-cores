package com.tiger.cores.dtos.requests;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest<T> {
    private T data;
}
