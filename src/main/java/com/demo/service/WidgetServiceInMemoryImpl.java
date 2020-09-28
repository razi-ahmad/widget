package com.demo.service;

import com.demo.domain.Widget;
import com.demo.domain.ZIndex;
import com.demo.dto.PageRequest;
import com.demo.dto.Point;
import com.demo.dto.WidgetDto;
import com.demo.exception.ApiBaseErrorCode;
import com.demo.exception.ApiException;
import com.demo.mapper.WidgetMapper;
import com.demo.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Profile("memory")
public class WidgetServiceInMemoryImpl implements WidgetService {

    private static final Map<UUID, Widget> widgetBucket = new ConcurrentHashMap<>();

    private static final TreeSet<ZIndex> zIndexes = new TreeSet<>();

    private final WidgetMapper mapper;

    @Autowired
    public WidgetServiceInMemoryImpl(WidgetMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Widget save(WidgetDto dto) {
        Widget widget = mapper.map(UUID.randomUUID(), dto);
        widget.setLastModification(Instant.now());

        if (ObjectUtils.isEmpty(widget.getZIndex())
                || ObjectUtils.isEmpty(widget.getZIndex().getIndex())) {
            ZIndex zIndex = ZIndex.builder().uuid(widget.getUuid()).build();
            widget.setZIndex(zIndex);
            addForeground(zIndex);
        } else {
            widget.getZIndex().setUuid(widget.getUuid());
            putWithZIndex(widget);
        }

        widgetBucket.put(widget.getUuid(), widget);
        return widget;
    }

    private synchronized void putWithZIndex(Widget widget) {
        if (zIndexes.contains(widget.getZIndex())) {
            moveToUpwards(widget);
        }
        zIndexes.add(widget.getZIndex());
    }

    private void addForeground(ZIndex zIndex) {
        if (widgetBucket.isEmpty()) {
            zIndex.setIndex(1);
        } else {
            zIndex.setIndex(zIndexes.last().getIndex() + 1);
        }
        zIndexes.add(zIndex);
    }

    private void moveToUpwards(Widget entity) {
        Iterator<ZIndex> iterator = zIndexes.iterator();
        Collection<ZIndex> subset = null;
        while (iterator.hasNext()) {
            ZIndex zIndex = iterator.next();
            if (zIndex.compareTo(entity.getZIndex()) == 0) {
                subset = zIndexes.subSet(zIndex, Boolean.TRUE, zIndexes.last(), Boolean.TRUE);
                break;
            }
        }
        assert subset != null;
        move(subset);
    }


    @Override
    public Widget update(UUID uuid, WidgetDto dto) {
        Widget request = mapper.map(uuid, dto);
        Optional<Widget> optional = get(uuid);
        if (optional.isPresent()) {
            return widgetBucket.computeIfPresent(uuid, (id, widget) -> {
                try {
                    Widget w = widget.clone();
                    w.getCoordinates().setX(request.getCoordinates().getX());
                    w.getCoordinates().setY(request.getCoordinates().getY());
                    w.setWidth(request.getWidth());
                    w.setHeight(request.getHeight());
                    return w;
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return widget;
                }
            });
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Widget not found", ApiBaseErrorCode.NotFound);
        }
    }

    @Override
    public void delete(UUID uuid) {
        Widget widget = widgetBucket.remove(uuid);
        zIndexes.remove(widget.getZIndex());
    }

    @Override
    public Optional<Widget> get(UUID uuid) {
        return Optional.ofNullable(widgetBucket.get(uuid));
    }

    @Override
    public List<Widget> list() {
        return zIndexes.stream().map(zIndex -> widgetBucket.get(zIndex.getUuid())).collect(Collectors.toList());
    }

    @Override
    public List<Widget> list(PageRequest pageRequest) {
        Pageable pageable = new Pageable(list());
        pageable.setPage(pageRequest.getPageNumber());
        pageable.setPageSize(pageRequest.getPageSize());
        return pageable.getContent();
    }

    @Override
    public List<Widget> filter(Point pointA, Point pointC) {
        return filterWidgets(widgetBucket.values(), pointA, pointC);
    }

    public static Collection<Widget> getWidgets() {
        return Collections.unmodifiableCollection(widgetBucket.values());
    }

    public static void clear() {
        widgetBucket.clear();
        zIndexes.clear();
    }

    public static int getWidgetSize() {
        return widgetBucket.size();
    }

    public static int getZIndicesSize() {
        return widgetBucket.size();
    }
}
