package com.tiger.cores.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestDataDto {
    private String userId;
    private String requestPath;
    private String requestBody;
    private LocalDateTime timestamp;
}
