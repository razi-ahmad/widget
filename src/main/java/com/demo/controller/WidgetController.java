package com.demo.controller;

import com.demo.domain.Widget;
import com.demo.dto.FilterRequest;
import com.demo.dto.PageRequest;
import com.demo.dto.WidgetDto;
import com.demo.service.WidgetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Api(tags = "Widget Controller")
public class WidgetController extends BaseController {

    private static final String METHOD_CREATE = "widget.create";
    private static final String METHOD_UPDATE = "widget.update";
    private static final String METHOD_DELETE = "widget.delete";
    private static final String METHOD_GET = "widget.get";
    private static final String METHOD_LIST = "widget.list";
    private static final String METHOD_LIST_PAGING = "widget.list.paging";

    private final WidgetService service;

    @Autowired
    public WidgetController(WidgetService service) {
        this.service = service;
    }

    @PostMapping("/widgets")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create Widget", response = Widget.class)
    public Widget create(HttpServletRequest request, @Validated @RequestBody WidgetDto dto) {
        checkThrottle(request, METHOD_CREATE);
        return service.save(dto);
    }

    @PutMapping("/widgets/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update Widget", response = Widget.class)
    public Widget update(@PathVariable("id") UUID uuid, HttpServletRequest request, @Valid @RequestBody WidgetDto dto) {
        checkThrottle(request, METHOD_UPDATE);
        return service.update(uuid, dto);
    }

    @DeleteMapping("/widgets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete Widget")
    public void delete(@PathVariable("id") UUID uuid, HttpServletRequest request) {
        checkThrottle(request, METHOD_DELETE);
        service.delete(uuid);
    }

    @GetMapping("/widgets/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get Widget", response = Widget.class)
    public Optional<Widget> get(@PathVariable("id") UUID uuid, HttpServletRequest request) {
        checkThrottle(request, METHOD_GET);
        return service.get(uuid);
    }

    @GetMapping("/widgets")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List All Widgets", response = List.class)
    public List<Widget> list(HttpServletRequest request) {
        checkThrottle(request, METHOD_LIST);
        return service.list();
    }

    @GetMapping("/widgets/paging")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List Widgets With Paging", response = List.class)
    public List<Widget> listWithPaging(HttpServletRequest request,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        checkThrottle(request, METHOD_LIST_PAGING);
        return service.list(PageRequest.of(page, pageSize));
    }

    @PostMapping("/widgets/filter")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "List Widgets With Paging", response = List.class)
    public List<Widget> listWithPaging(HttpServletRequest request, @Valid @RequestBody FilterRequest filterRequest) {
        checkThrottle(request, METHOD_LIST_PAGING);
        return service.filter(filterRequest.getPointA(), filterRequest.getPointC());
    }
}
