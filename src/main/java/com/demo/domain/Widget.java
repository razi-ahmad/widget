package com.demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Widget implements Serializable, Cloneable {

    @Id
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(name = "width", nullable = false)
    private Double width;

    @Column(name = "height", nullable = false)
    private Double height;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uuid", nullable = false)
    private ZIndex zIndex;

    @Embedded
    private Coordinate coordinates;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private Instant lastModification;

    @Override
    public Widget clone() throws CloneNotSupportedException {
        Widget widget = (Widget) super.clone();
        widget.setCoordinates(Coordinate.builder().x(this.coordinates.getX()).y(this.coordinates.getY()).build());
        return widget;
    }
}
