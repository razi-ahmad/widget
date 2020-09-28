package com.demo.service;

import com.demo.domain.Widget;
import com.demo.domain.ZIndex;
import com.demo.dto.PageRequest;
import com.demo.dto.Point;
import com.demo.dto.WidgetDto;
import com.demo.exception.ApiBaseErrorCode;
import com.demo.exception.ApiException;
import com.demo.mapper.WidgetMapper;
import com.demo.repository.WidgetRepository;
import com.demo.repository.ZIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile("db")
public class WidgetServiceDbImpl implements WidgetService {

    private final WidgetMapper mapper;

    private final WidgetRepository repository;

    private final ZIndexRepository zIndexRepository;

    @Autowired
    public WidgetServiceDbImpl(WidgetMapper mapper, WidgetRepository repository, ZIndexRepository zIndexRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.zIndexRepository = zIndexRepository;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Widget save(WidgetDto dto) {
        Widget entity = mapper.map(UUID.randomUUID(), dto);
        if (ObjectUtils.isEmpty(entity.getZIndex())
                || ObjectUtils.isEmpty(entity.getZIndex().getIndex())) {
            ZIndex zIndex = ZIndex.builder().uuid(entity.getUuid()).build();
            entity.setZIndex(zIndex);
            addForeground(zIndex);
        } else {
            entity.getZIndex().setUuid(entity.getUuid());
            if (zIndexRepository.existsByIndex(entity.getZIndex().getIndex())) {
                moveToUpward(entity.getZIndex().getIndex());
            }
        }

        return repository.save(entity);
    }

    private void addForeground(ZIndex zIndex) {
        if (zIndexRepository.count() == 0) {
            zIndex.setIndex(1);
        } else {
            zIndex.setIndex(zIndexRepository.findMaxZIndex() + 1);
        }
    }

    private void moveToUpward(Integer index) {
        Collection<ZIndex> zIndices = zIndexRepository.findAllByIndex(index);
        move(zIndices);
        zIndexRepository.saveAll(zIndices);
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Widget update(UUID uuid, WidgetDto dto) {
        Widget widget = mapper.map(uuid, dto);
        Optional<Widget> optional = repository.findById(widget.getUuid());
        if (optional.isPresent()) {
            Widget entity = optional.get();
            entity.getCoordinates().setX(widget.getCoordinates().getX());
            entity.getCoordinates().setY(widget.getCoordinates().getY());
            entity.setWidth(widget.getWidth());
            entity.setHeight(widget.getHeight());
            repository.save(entity);
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Widget not found", ApiBaseErrorCode.NotFound);
        }
        return widget;
    }

    @Override
    public void delete(UUID uuid) {
        repository.deleteById(uuid);
    }

    @Override
    public Optional<Widget> get(UUID uuid) {
        return repository.findById(uuid);
    }

    @Override
    public List<Widget> list() {
        return repository.getAllOrderByZIndex();
    }

    @Override
    public List<Widget> list(PageRequest pageRequest) {
        return repository.getAllOrderByZIndex(pageRequest).getContent();
    }

    @Override
    public List<Widget> filter(Point pointA, Point pointC) {
        return filterWidgets(list(), pointA, pointC);
    }
}
