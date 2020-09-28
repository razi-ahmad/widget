package com.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ZIndex implements Comparable<ZIndex>, Serializable {

    @Id
    @Column(name = "uuid", nullable = false)
    @JsonIgnore
    private UUID uuid;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZIndex)) return false;
        ZIndex zIndex = (ZIndex) o;
        return Objects.equals(getIndex(), zIndex.getIndex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex());
    }

    @Override
    public int compareTo(ZIndex zIndex) {
        return index.compareTo(zIndex.getIndex());
    }
}
