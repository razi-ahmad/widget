package com.demo.service;

import com.demo.domain.Coordinate;
import com.demo.domain.Widget;
import com.demo.domain.ZIndex;
import com.demo.dto.PageRequest;
import com.demo.exception.ApiException;
import com.demo.mapper.WidgetMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WidgetServiceInMemoryImplTest {

    @Spy
    private WidgetMapper mapper;

    private WidgetServiceInMemoryImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new WidgetServiceInMemoryImpl(mapper);
        buildWidget();
    }

    @AfterEach
    void tearDown() {
        WidgetServiceInMemoryImpl.clear();
    }

    @Test
    public void test_save() {
        Widget widget = Widget.builder()
                .width(10D)
                .height(10D)
                .coordinates(Coordinate.builder().x(10).y(10).build()).build();
        Widget result = service.save(mapper.map(widget));
        assertNotNull(result);
        assertNotNull(result.getLastModification());
        assertNotNull(result.getCoordinates());
        assertNotNull(result.getCoordinates().getX());
        assertNotNull(result.getCoordinates().getY());
        assertEquals(widget.getWidth(), result.getWidth());
        assertEquals(widget.getHeight(), result.getHeight());
    }

    @Test
    public void test_save_moveUpward() {
        assertEquals(4, WidgetServiceInMemoryImpl.getWidgetSize());
        assertEquals(4, WidgetServiceInMemoryImpl.getZIndicesSize());
    }

    @Test
    public void test_update() throws CloneNotSupportedException {
        Widget widget = WidgetServiceInMemoryImpl.getWidgets().iterator().next().clone();
        widget.setHeight(20D);
        widget.setWidth(20D);
        Widget result = service.update(widget.getUuid(), mapper.map(widget));
        assertNotNull(result);
    }

    @Test
    public void test_delete() {
        service.delete(WidgetServiceInMemoryImpl.getWidgets().iterator().next().getUuid());
        assertEquals(3, WidgetServiceInMemoryImpl.getWidgetSize());
        assertEquals(WidgetServiceInMemoryImpl.getZIndicesSize(), WidgetServiceInMemoryImpl.getWidgetSize());
    }

    @Test
    public void test_get() {
        Optional<Widget> result = service.get(WidgetServiceInMemoryImpl.getWidgets().iterator().next().getUuid());
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    public void test_list() {
        List<Widget> widgets = service.list();
        assertTrue(
                Arrays.equals(
                        new int[]{1, 2, 3, 4},
                        widgets.stream().mapToInt(widget -> widget.getZIndex().getIndex()).toArray()));
        assertTrue(Boolean.TRUE);
    }

    @Test
    public void test_listWhenPaging() {
        List<Widget> result = service.list(PageRequest.of(1, 10));
        assertEquals(4, result.size());
    }

    @Test
    public void test_listWhenWrongPagesize() {
        assertThrows(ApiException.class, () -> service.list(PageRequest.of(1, 501)));
    }

    private void buildWidget() {
        service.save(
                mapper.map(Widget.builder()
                        .width(10D)
                        .height(10D)
                        .coordinates(Coordinate.builder().x(10).y(10).build())
                        .zIndex(ZIndex.builder().index(1).build())
                        .build()));
        service.save(
                mapper.map(Widget.builder()
                        .width(30D)
                        .height(30D)
                        .coordinates(Coordinate.builder().x(30).y(30).build())
                        .zIndex(ZIndex.builder().index(2).build())
                        .build()));

        service.save(
                mapper.map(Widget.builder()
                        .width(40D)
                        .height(40D)
                        .coordinates(Coordinate.builder().x(40).y(40).build())
                        .build()));

        service.save(
                mapper.map(Widget.builder()
                        .width(20D)
                        .height(20D)
                        .coordinates(Coordinate.builder().x(20).y(20).build())
                        .zIndex(ZIndex.builder().index(2).build())
                        .build()));
    }
}
