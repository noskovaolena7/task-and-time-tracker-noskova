package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private String message;
    private String code;
}
