package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyCreateRequestDto {

    private String name;
    private String description;

}
