package com.demo.service;

import com.demo.domain.Coordinate;
import com.demo.domain.Widget;
import com.demo.domain.ZIndex;
import com.demo.dto.PageRequest;
import com.demo.mapper.WidgetMapper;
import com.demo.repository.WidgetRepository;
import com.demo.repository.ZIndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WidgetServiceDbImplTest {

    private WidgetServiceDbImpl service;

    @Mock
    private WidgetRepository repository;

    @Mock
    private ZIndexRepository zIndexRepository;

    @Spy
    private WidgetMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new WidgetServiceDbImpl(mapper, repository, zIndexRepository);
    }

    @Test
    public void test_save() {
        Widget widget = Widget.builder()
                .uuid(UUID.randomUUID())
                .width(10D)
                .height(10D)
                .zIndex(ZIndex.builder().index(1).build())
                .coordinates(Coordinate.builder().x(10).y(10).build()).build();
        Mockito.when(zIndexRepository.count()).thenReturn((long) 0);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(widget);
        Widget result = service.save(mapper.map(widget));
        assertNotNull(result);
        assertNotNull(result.getUuid());
        assertNotNull(result.getZIndex());
        assertNotNull(result.getCoordinates());
    }

    @Test
    public void test_update() {
        Widget widget = Widget.builder()
                .uuid(UUID.randomUUID())
                .width(10D)
                .height(10D)
                .lastModification(Instant.now())
                .lastModification(Instant.now())
                .zIndex(ZIndex.builder().index(1).build())
                .coordinates(Coordinate.builder().x(20).y(20).build()).build();
        Mockito.when(repository.findById(ArgumentMatchers.any())).thenReturn(Optional.ofNullable(widget));
        Widget result = service.update(widget.getUuid(), mapper.map(widget));
        assertNotNull(result);
        assertEquals(10, result.getWidth());
    }

    @Test
    public void test_delete() {
        service.delete(ArgumentMatchers.any());
        assertTrue(Boolean.TRUE);
    }

    @Test
    public void test_get() {
        Widget widget = Widget.builder()
                .uuid(UUID.randomUUID())
                .width(10D)
                .height(10D)
                .lastModification(Instant.now())
                .lastModification(Instant.now())
                .zIndex(ZIndex.builder().index(1).build())
                .coordinates(Coordinate.builder().x(10).y(10).build()).build();
        Mockito.when(repository.findById(ArgumentMatchers.any())).thenReturn(Optional.ofNullable(widget));

        Optional<Widget> result = service.get(widget.getUuid());
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    public void test_list() {
        Mockito.when(repository.getAllOrderByZIndex()).thenReturn(buildWidget());
        List<Widget> result = service.list();
        assertFalse(result.isEmpty());
    }

    @Test
    public void test_listWhenPaging() {
        Mockito.when(repository.getAllOrderByZIndex(ArgumentMatchers.any())).thenReturn(new PageImpl<>(buildWidget()));
        List<Widget> result = service.list(PageRequest.of(1, 10));
        assertFalse(result.isEmpty());
    }

    private List<Widget> buildWidget() {
        return Arrays.asList(
                Widget.builder()
                        .width(10D)
                        .height(10D)
                        .uuid(UUID.randomUUID())
                        .lastModification(Instant.now())
                        .coordinates(Coordinate.builder().x(10).y(10).build())
                        .zIndex(ZIndex.builder().index(1).build())
                        .build(),
                Widget.builder()
                        .width(30D)
                        .height(30D)
                        .uuid(UUID.randomUUID())
                        .lastModification(Instant.now())
                        .coordinates(Coordinate.builder().x(30).y(30).build())
                        .zIndex(ZIndex.builder().index(2).build())
                        .build(),
                Widget.builder()
                        .width(40D)
                        .height(40D)
                        .uuid(UUID.randomUUID())
                        .lastModification(Instant.now())
                        .coordinates(Coordinate.builder().x(40).y(40).build())
                        .zIndex(ZIndex.builder().index(3).build())
                        .build(),
                Widget.builder()
                        .width(20D)
                        .height(20D)
                        .uuid(UUID.randomUUID())
                        .lastModification(Instant.now())
                        .coordinates(Coordinate.builder().x(20).y(20).build())
                        .zIndex(ZIndex.builder().index(4).build())
                        .build());
    }
}