package com.demo.controller;

import com.demo.WidgetApplication;
import com.demo.config.TestRedisConfiguration;
import com.demo.domain.Widget;
import com.demo.dto.FilterRequest;
import com.demo.dto.Point;
import com.demo.dto.WidgetDto;
import com.demo.service.WidgetServiceInMemoryImpl;
import com.demo.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {TestRedisConfiguration.class, WidgetApplication.class})
public class WidgetControllerIT {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    public void tearDown() {
        if (jdbcTemplate != null) {
            TestUtils.cleanUpData(jdbcTemplate.getDataSource(), true);
        } else {
            WidgetServiceInMemoryImpl.clear();
        }
    }

    @Test
    public void test_ShouldCreateWidget() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(10D)
                .height(10D)
                .point(Point.builder().x(10).y(10).build())
                .build();
        mvc.perform(post("/widgets")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    public void test_ShouldUpdateWidget() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(10d)
                .height(10D)
                .point(Point.builder().x(10).y(10).build())
                .build();
        String result = mvc.perform(post("/widgets")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
        Widget widget = mapper.readValue(result, Widget.class);
        mvc.perform(put("/widgets/" + widget.getUuid())
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testShouldDeleteWidget() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(10D)
                .height(10D)
                .point(Point.builder().x(10).y(10).build())
                .build();
        String response = mvc.perform(post("/widgets")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
        Widget widget = mapper.readValue(response, Widget.class);
        mvc.perform(delete("/widgets/" + widget.getUuid())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    public void testShouldGetWidget() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(10D)
                .height(10D)
                .point(Point.builder().x(10).y(10).build())
                .build();
        String result = mvc.perform(post("/widgets")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
        Widget widget = mapper.readValue(result, Widget.class);
        mvc.perform(get("/widgets/" + widget.getUuid())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void testShouldReturnList() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(10D)
                .height(10D)
                .point(Point.builder().x(10).y(10).build())
                .build();
        mvc.perform(post("/widgets")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        mvc.perform(get("/widgets")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void testShouldReturnListWithPaging() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(10D)
                .height(10D)
                .point(Point.builder().x(10).y(10).build())
                .build();
        mvc.perform(post("/widgets?page=1&pageSize=10")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        mvc.perform(get("/widgets")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void testShouldReturnListWithFilter() throws Exception {
        WidgetDto request = WidgetDto.builder()
                .width(100D)
                .height(100D)
                .point(Point.builder().x(50).y(50).build())
                .index(1)
                .build();
        mvc.perform(post("/widgets?page=1&pageSize=10")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        request.setPoint(Point.builder().x(50).y(100).build());
        mvc.perform(post("/widgets?page=1&pageSize=10")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        request.setPoint(Point.builder().x(100).y(100).build());
        mvc.perform(post("/widgets?page=1&pageSize=10")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

        String response = mvc.perform(post("/widgets/filter")
                .content(
                        mapper.writeValueAsString(FilterRequest.builder()
                                .pointA(Point.builder().x(0).y(0).build())
                                .pointC(Point.builder().x(100).y(150).build())
                                .build()))
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
        List filteredList = mapper.readValue(response, List.class);
        Assertions.assertEquals(2, filteredList.size());
    }
}