package com.demo.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Coordinate implements Serializable {

    @Column(name = "x", nullable = false)
    private Integer x;


    @Column(name = "y", nullable = false)
    private Integer y;
}
