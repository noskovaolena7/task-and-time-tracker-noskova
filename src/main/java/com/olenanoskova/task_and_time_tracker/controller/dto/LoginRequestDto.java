package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;
import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
public class LoginRequestDto {
     private String email;
     private String password;
}