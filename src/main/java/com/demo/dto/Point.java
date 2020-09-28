package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point implements Serializable {

    @NotNull(message = "Point x cannot be null")
    private Integer x;

    @NotNull(message = "Point y cannot be null")
    private Integer y;
}
