package com.demo.mapper;

import com.demo.domain.Coordinate;
import com.demo.domain.Widget;
import com.demo.domain.ZIndex;
import com.demo.dto.Point;
import com.demo.dto.WidgetDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WidgetMapper {

    public Widget map(UUID uuid, WidgetDto dto) {
        return Widget
                .builder()
                .uuid(uuid)
                .width(dto.getWidth())
                .height(dto.getHeight())
                .coordinates(Coordinate.builder().x(dto.getPoint().getX()).y(dto.getPoint().getY()).build())
                .zIndex(dto.getIndex() != null ? ZIndex.builder().index(dto.getIndex()).build() : null)
                .build();
    }

    public WidgetDto map(Widget widget) {
        return WidgetDto.builder()
                .width(widget.getWidth())
                .height(widget.getHeight())
                .index(widget.getZIndex() != null ? widget.getZIndex().getIndex() : null)
                .point(Point.builder().x(widget.getCoordinates().getX()).y(widget.getCoordinates().getY()).build())
                .build();
    }
}
