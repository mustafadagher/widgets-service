package com.mustafadagher.widgets.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.service.WidgetsService;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WidgetsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WidgetsApiController widgetsApiController;

    @SpyBean
    private WidgetsService widgetsService;

    private static List<Widget> savedWidgets = new ArrayList<>();

    @Test
    @Order(1)
    void testContextLoads() {
        assertThat(widgetsApiController).isNotNull();
    }

    @Test
    @Order(2)
    void testWidgetRequestArgumentsShouldNotBeNull() throws Exception {
        // Given invalid widgetRequest (all required values are nulls)
        WidgetRequest widgetRequest = new WidgetRequest();

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasItems("height must not be null", "x must not be null", "y must not be null", "width must not be null")));
    }

    @Test
    @Order(3)
    void testWidgetWidthAndHeightMustBeGreaterThanZero() throws Exception {
        // Given invalid widgetRequest (all required values are nulls)
        WidgetRequest widgetRequest = aValidWidgetRequest();
        widgetRequest.setHeight(0f);
        widgetRequest.setWidth(0f);

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasItems("height must be greater than 0", "width must be greater than 0")));
    }

    @Test
    @Order(4)
    void testCreateWidgetWillReturnsCompleteWidgetDescription() throws Exception {
        // Given
        WidgetRequest widgetRequest = aValidWidgetRequest();

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        savedWidgets.add(objectMapper.readValue(responseString, Widget.class));

        // Then
        verify(widgetsService).addWidget(widgetRequest);

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists())
                .andExpect(jsonPath("$.x", is(widgetRequest.getX()), Long.class))
                .andExpect(jsonPath("$.y", is(widgetRequest.getY()), Long.class))
                .andExpect(jsonPath("$.z", is(widgetRequest.getZ()), Long.class))
                .andExpect(jsonPath("$.width", is(widgetRequest.getWidth()), Float.class))
                .andExpect(jsonPath("$.height", is(widgetRequest.getHeight()), Float.class));

    }

    @Test
    @Order(5)
    void testZIndexIsSetIfLeftNotSpecifiedInRequest() throws Exception {
        // Given
        WidgetRequest widgetRequestWithNoZ = aValidWidgetRequest().z(null);

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequestWithNoZ))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        savedWidgets.add(objectMapper.readValue(responseString, Widget.class));

        // Then
        verify(widgetsService).addWidget(widgetRequestWithNoZ);

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists())
                .andExpect(jsonPath("$.z").exists())
                .andExpect(jsonPath("$.z", matchesRegex("^-?\\d{1,19}$"), String.class))
                .andExpect(jsonPath("$.z", IsNot.not(widgetRequestWithNoZ.getZ()), Long.class));
    }

    @Test
    @Order(6)
    void testGetWidgetByIdReturnsFullWidgetDescription() throws Exception {
        // Given
        Widget savedWidget = savedWidgets.get(0);
        UUID id = savedWidget.getId();

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets/{id}", id));

        // Then
        verify(widgetsService).getWidgetById(id);

        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(id.toString())))
                        .andExpect(jsonPath("$.x", is(savedWidget.getX()), Long.class))
                        .andExpect(jsonPath("$.y", is(savedWidget.getY()), Long.class))
                        .andExpect(jsonPath("$.z", is(savedWidget.getZ()), Long.class))
                        .andExpect(jsonPath("$.width", is(savedWidget.getWidth()), Float.class))
                        .andExpect(jsonPath("$.height", is(savedWidget.getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        Widget widget = objectMapper.readValue(responseString, Widget.class);

        assertThat(widget.getLastModificationDate())
                .isAtSameInstantAs(savedWidget.getLastModificationDate());
    }

    @Test
    @Order(7)
    void testGetWidgetByNonExistingIdReturns404() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets/{id}", id));

        // Then
        verify(widgetsService).getWidgetById(id);

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No Widgets found with the specified id")));
    }

    @Test
    @Order(8)
    void testGetAllWidgetsReturnsWidgetsSortedByZ() throws Exception {
        // Given
        addAThirdWidgetWithZEqualNegative5();

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets"));

        // Then
        verify(widgetsService).getAllWidgets();

        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray())
                        .andExpect(jsonPath("$.*", hasSize(3)))
                        .andExpect(jsonPath("$.[0].id", is(savedWidgets.get(2).getId().toString())))
                        .andExpect(jsonPath("$[0].x", is(savedWidgets.get(2).getX()), Long.class))
                        .andExpect(jsonPath("$[0].y", is(savedWidgets.get(2).getY()), Long.class))
                        .andExpect(jsonPath("$[0].z", is(savedWidgets.get(2).getZ()), Long.class))
                        .andExpect(jsonPath("$[0].width", is(savedWidgets.get(2).getWidth()), Float.class))
                        .andExpect(jsonPath("$[0].height", is(savedWidgets.get(2).getHeight()), Float.class))
                        .andExpect(jsonPath("$[1].id", is(savedWidgets.get(0).getId().toString())))
                        .andExpect(jsonPath("$[1].x", is(savedWidgets.get(0).getX()), Long.class))
                        .andExpect(jsonPath("$[1].y", is(savedWidgets.get(0).getY()), Long.class))
                        .andExpect(jsonPath("$[1].z", is(savedWidgets.get(0).getZ()), Long.class))
                        .andExpect(jsonPath("$[1].width", is(savedWidgets.get(0).getWidth()), Float.class))
                        .andExpect(jsonPath("$[1].height", is(savedWidgets.get(0).getHeight()), Float.class))
                        .andExpect(jsonPath("$[2].id", is(savedWidgets.get(1).getId().toString())))
                        .andExpect(jsonPath("$[2].x", is(savedWidgets.get(1).getX()), Long.class))
                        .andExpect(jsonPath("$[2].y", is(savedWidgets.get(1).getY()), Long.class))
                        .andExpect(jsonPath("$[2].z", is(savedWidgets.get(1).getZ()), Long.class))
                        .andExpect(jsonPath("$[2].width", is(savedWidgets.get(1).getWidth()), Float.class))
                        .andExpect(jsonPath("$[2].height", is(savedWidgets.get(1).getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<Widget> widgets = objectMapper.readValue(responseString, new TypeReference<List<Widget>>() {});

        assertThat(widgets).hasSize(3);

        assertThat(widgets.get(0).getLastModificationDate())
                .isAtSameInstantAs(savedWidgets.get(2).getLastModificationDate());
    }

    @Test
    @Order(9)
    void testDeleteWidgetByIdReturns204() throws Exception {
        // Given
        UUID id = savedWidgets.get(0).getId();

        // When
        ResultActions result = mockMvc
                .perform(delete("/widgets/{id}", id));
        savedWidgets.remove(0);

        // Then
        verify(widgetsService).deleteWidget(id);

        result.andExpect(status().isNoContent());
    }

    @Test
    @Order(10)
    void testDeleteWidgetByNonExistingIdReturns404() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        ResultActions result = mockMvc
                .perform(delete("/widgets/{id}", id));

        // Then
        verify(widgetsService).deleteWidget(id);

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No Widgets found with the specified id")));
    }

    @Test
    @Order(11)
    void testUpdateWidgetWillReturnsCompleteWidgetDescription() throws Exception {
        // Given
        WidgetRequest widgetRequest = new WidgetRequest().z(14L).x(2L).y(16L).height(23.4F).width(11.6F);
        UUID id = savedWidgets.get(0).getId();
        OffsetDateTime lastModificationDate = savedWidgets.get(0).getLastModificationDate();

        // When
        ResultActions result = mockMvc
                .perform(
                        put("/widgets/{id}", id)
                                .content(objectMapper.writeValueAsString(widgetRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        Widget updated = objectMapper.readValue(responseString, Widget.class);
        savedWidgets.set(0, updated);

        // Then
        verify(widgetsService).updateWidgetById(id, widgetRequest);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.x", is(widgetRequest.getX()), Long.class))
                .andExpect(jsonPath("$.y", is(widgetRequest.getY()), Long.class))
                .andExpect(jsonPath("$.z", is(widgetRequest.getZ()), Long.class))
                .andExpect(jsonPath("$.width", is(widgetRequest.getWidth()), Float.class))
                .andExpect(jsonPath("$.height", is(widgetRequest.getHeight()), Float.class));

        assertThat(updated.getLastModificationDate())
                .isAfter(lastModificationDate);

    }

    @Test
    @Order(12)
    void testUpdateWidgetByNonExistingIdReturns404() throws Exception {
        // Given
        WidgetRequest widgetRequest = new WidgetRequest().z(14L).x(2L).y(16L).height(23.4F).width(11.6F);
        UUID id = UUID.randomUUID();

        // When
        ResultActions result = mockMvc
                .perform(
                        put("/widgets/{id}", id)
                                .content(objectMapper.writeValueAsString(widgetRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());


        // Then
        verify(widgetsService).updateWidgetById(id, widgetRequest);

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No Widgets found with the specified id")));


    }

    private void addAThirdWidgetWithZEqualNegative5() throws Exception {
        WidgetRequest widgetRequestWithNoZ = aValidWidgetRequest().z(-5L);

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequestWithNoZ))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        savedWidgets.add(objectMapper.readValue(responseString, Widget.class));
    }
}
