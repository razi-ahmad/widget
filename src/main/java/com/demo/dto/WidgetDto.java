package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WidgetDto {

    @NotNull(message = "Width must not be null")
    private Double width;

    @NotNull(message = "Height must not be null")
    private Double height;

    private Integer index;

    @Valid
    @NotNull(message = "Point must not be null")
    private Point point;
}
