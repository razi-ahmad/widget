package com.demo.service;

import com.demo.domain.Coordinate;
import com.demo.domain.Widget;
import com.demo.domain.ZIndex;
import com.demo.dto.PageRequest;
import com.demo.dto.Point;
import com.demo.dto.WidgetDto;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public interface WidgetService {

    Widget save(WidgetDto dto);

    Widget update(UUID uuid, WidgetDto dto);

    void delete(UUID uuid);

    Optional<Widget> get(UUID uuid);

    List<Widget> list();

    List<Widget> list(PageRequest pageRequest);

    List<Widget> filter(Point pointA, Point pointC);

    default void move(Collection<ZIndex> zIndices) {
        for (ZIndex zIndex : zIndices) {
            zIndex.setIndex(zIndex.getIndex() + 1);
        }
    }

    default Rectangle2D getRectangle(Point pointA, Point pointC) {
        Point pointB = Point.builder().x(pointC.getX()).y(pointA.getY()).build();
        Point pointD = Point.builder().x(pointA.getX()).y(pointC.getY()).build();
        Coordinate center = Coordinate.builder().x((pointA.getX() + pointC.getX()) / 2).y((pointA.getY() + pointC.getY()) / 2).build();
        double d1 = Math.sqrt(Math.pow((pointA.getX() - pointB.getX()), 2) + Math.pow((pointA.getY() - pointB.getY()), 2));
        double d2 = Math.sqrt(Math.pow((pointA.getX() - pointD.getX()), 2) + Math.pow((pointA.getY() - pointD.getY()), 2));
        double height = Math.max(d1, d2);
        double width = Math.min(d1, d2);
        Widget widget = Widget.builder().coordinates(center).width(width).height(height).build();
        return new Rectangle2D.Double(pointA.getX(), pointA.getY(), widget.getWidth(), widget.getHeight());
    }

    default List<Widget> filterWidgets(Collection<Widget> values, Point pointA, Point pointC) {
        Rectangle2D r = getRectangle(pointA, pointC);
        return values.stream().filter(widget -> {
            Rectangle2D s = new Rectangle2D.Double(
                    widget.getCoordinates().getX() - widget.getWidth() / 2,
                    widget.getCoordinates().getY() - widget.getHeight() / 2,
                    widget.getWidth(),
                    widget.getHeight());
            return r.contains(s);
        }).collect(Collectors.toList());
    }
}
