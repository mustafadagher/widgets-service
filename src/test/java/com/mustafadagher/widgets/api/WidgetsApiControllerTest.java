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
import java.util.*;

import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/***
 * Integration tests for the controller end-points
 *
 * I kept order on each method because some of the integration test methods are dependant on the result of previous ones.
 * I having test methods that are dependant on one another isn't a best practice,
 * but I kept it this way here just because it was easier in the moment
 */
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

    private static Map<UUID, Widget> savedWidgets = new LinkedHashMap<>();

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
                .andExpect(jsonPath("$.message", is("Request contains invalid parameters or body")))
                .andExpect(jsonPath("$.errors",
                        hasItems("height must not be null", "width must not be null", "x must not be null", "y must not be null")));
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
                .andExpect(jsonPath("$.message", is("Request contains invalid parameters or body")))
                .andExpect(jsonPath("$.errors", hasItems("height must be greater than 0", "width must be greater than 0")));
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
        Widget saved = objectMapper.readValue(responseString, Widget.class);
        savedWidgets.put(saved.getId(), saved);

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
        WidgetRequest widgetRequestWithNoZ = aValidWidgetRequest().x(50L).y(100L).z(null);

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequestWithNoZ))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        Widget saved = objectMapper.readValue(responseString, Widget.class);
        savedWidgets.put(saved.getId(), saved);

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
        Widget[] widgets = savedWidgets.values().toArray(new Widget[0]);
        Widget savedWidget = widgets[0];
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
        verify(widgetsService).getAllWidgets(anyInt(), anyInt(), any());
        List<Widget> savedWidgetList = new ArrayList<>(savedWidgets.values());
        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray())
                        .andExpect(jsonPath("$.*", hasSize(3)))
                        .andExpect(jsonPath("$[0].id", is(savedWidgetList.get(2).getId().toString())))
                        .andExpect(jsonPath("$[0].x", is(savedWidgetList.get(2).getX()), Long.class))
                        .andExpect(jsonPath("$[0].y", is(savedWidgetList.get(2).getY()), Long.class))
                        .andExpect(jsonPath("$[0].z", is(savedWidgetList.get(2).getZ()), Long.class))
                        .andExpect(jsonPath("$[0].width", is(savedWidgetList.get(2).getWidth()), Float.class))
                        .andExpect(jsonPath("$[0].height", is(savedWidgetList.get(2).getHeight()), Float.class))
                        .andExpect(jsonPath("$[1].id", is(savedWidgetList.get(0).getId().toString())))
                        .andExpect(jsonPath("$[1].x", is(savedWidgetList.get(0).getX()), Long.class))
                        .andExpect(jsonPath("$[1].y", is(savedWidgetList.get(0).getY()), Long.class))
                        .andExpect(jsonPath("$[1].z", is(savedWidgetList.get(0).getZ()), Long.class))
                        .andExpect(jsonPath("$[1].width", is(savedWidgetList.get(0).getWidth()), Float.class))
                        .andExpect(jsonPath("$[1].height", is(savedWidgetList.get(0).getHeight()), Float.class))
                        .andExpect(jsonPath("$[2].id", is(savedWidgetList.get(1).getId().toString())))
                        .andExpect(jsonPath("$[2].x", is(savedWidgetList.get(1).getX()), Long.class))
                        .andExpect(jsonPath("$[2].y", is(savedWidgetList.get(1).getY()), Long.class))
                        .andExpect(jsonPath("$[2].z", is(savedWidgetList.get(1).getZ()), Long.class))
                        .andExpect(jsonPath("$[2].width", is(savedWidgetList.get(1).getWidth()), Float.class))
                        .andExpect(jsonPath("$[2].height", is(savedWidgetList.get(1).getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<Widget> widgets = objectMapper.readValue(responseString, new TypeReference<List<Widget>>() {
        });

        assertThat(widgets).hasSize(3);

        assertThat(widgets.get(0).getLastModificationDate())
                .isAtSameInstantAs(savedWidgetList.get(2).getLastModificationDate());
    }

    @Test
    @Order(9)
    void testGetAllWidgetsPageZeroAndSizeTwoReturnsTwoWidgets() throws Exception {
        //Given three widgets has been inserted earlier

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .param("page", "0")
                        .param("size", "2")
                );

        // Then
        verify(widgetsService).getAllWidgets(anyInt(), anyInt(), any());
        List<Widget> savedWidgetList = new ArrayList<>(savedWidgets.values());
        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray())
                        .andExpect(jsonPath("$.*", hasSize(2)))
                        .andExpect(jsonPath("$[0].id", is(savedWidgetList.get(2).getId().toString())))
                        .andExpect(jsonPath("$[0].x", is(savedWidgetList.get(2).getX()), Long.class))
                        .andExpect(jsonPath("$[0].y", is(savedWidgetList.get(2).getY()), Long.class))
                        .andExpect(jsonPath("$[0].z", is(savedWidgetList.get(2).getZ()), Long.class))
                        .andExpect(jsonPath("$[0].width", is(savedWidgetList.get(2).getWidth()), Float.class))
                        .andExpect(jsonPath("$[0].height", is(savedWidgetList.get(2).getHeight()), Float.class))
                        .andExpect(jsonPath("$[1].id", is(savedWidgetList.get(0).getId().toString())))
                        .andExpect(jsonPath("$[1].x", is(savedWidgetList.get(0).getX()), Long.class))
                        .andExpect(jsonPath("$[1].y", is(savedWidgetList.get(0).getY()), Long.class))
                        .andExpect(jsonPath("$[1].z", is(savedWidgetList.get(0).getZ()), Long.class))
                        .andExpect(jsonPath("$[1].width", is(savedWidgetList.get(0).getWidth()), Float.class))
                        .andExpect(jsonPath("$[1].height", is(savedWidgetList.get(0).getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<Widget> widgets = objectMapper.readValue(responseString, new TypeReference<List<Widget>>() {
        });

        assertThat(widgets).hasSize(2);

        assertThat(widgets.get(0).getLastModificationDate())
                .isAtSameInstantAs(savedWidgetList.get(2).getLastModificationDate());
    }

    @Test
    @Order(10)
    void testGetAllWidgetsPageOneAndSizeTwoReturnsOneWidget() throws Exception {
        //Given three widgets has been inserted earlier

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .param("page", "1")
                        .param("size", "2")
                );

        // Then
        verify(widgetsService).getAllWidgets(anyInt(), anyInt(), any());
        List<Widget> savedWidgetList = new ArrayList<>(savedWidgets.values());
        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray())
                        .andExpect(jsonPath("$.*", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is(savedWidgetList.get(1).getId().toString())))
                        .andExpect(jsonPath("$[0].x", is(savedWidgetList.get(1).getX()), Long.class))
                        .andExpect(jsonPath("$[0].y", is(savedWidgetList.get(1).getY()), Long.class))
                        .andExpect(jsonPath("$[0].z", is(savedWidgetList.get(1).getZ()), Long.class))
                        .andExpect(jsonPath("$[0].width", is(savedWidgetList.get(1).getWidth()), Float.class))
                        .andExpect(jsonPath("$[0].height", is(savedWidgetList.get(1).getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<Widget> widgets = objectMapper.readValue(responseString, new TypeReference<List<Widget>>() {
        });

        assertThat(widgets).hasSize(1);

        assertThat(widgets.get(0).getLastModificationDate())
                .isAtSameInstantAs(savedWidgetList.get(1).getLastModificationDate());
    }

    @Test
    @Order(11)
    void testGetAllWidgetsWithSizeGreaterThan500ReturnsStatus400() throws Exception {
        //Given three widgets has been inserted earlier

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .param("page", "0")
                        .param("size", "501")
                );

        // Then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("getAllWidgets.size: must be less than or equal to 500")));

    }

    @Test
    @Order(12)
    void testGetAllWidgetsWithAreaFilterThatIncludesTwoWidgets() throws Exception {
        //Given three widgets has been inserted earlier with (X,Y) are (50,50), (50,100) and (100,100)

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .queryParam("leftX", "0")
                        .queryParam("rightX", "100")
                        .queryParam("lowerY", "0")
                        .queryParam("higherY", "150")
                );

        // Then
        verify(widgetsService).getAllWidgets(anyInt(), anyInt(), any());
        List<Widget> savedWidgetList = new ArrayList<>(savedWidgets.values());
        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray())
                        .andExpect(jsonPath("$.*", hasSize(2)))
                        .andExpect(jsonPath("$[0].id", is(savedWidgetList.get(0).getId().toString())))
                        .andExpect(jsonPath("$[0].x", is(savedWidgetList.get(0).getX()), Long.class))
                        .andExpect(jsonPath("$[0].y", is(savedWidgetList.get(0).getY()), Long.class))
                        .andExpect(jsonPath("$[0].z", is(savedWidgetList.get(0).getZ()), Long.class))
                        .andExpect(jsonPath("$[0].width", is(savedWidgetList.get(0).getWidth()), Float.class))
                        .andExpect(jsonPath("$[0].height", is(savedWidgetList.get(0).getHeight()), Float.class))
                        .andExpect(jsonPath("$[1].id", is(savedWidgetList.get(1).getId().toString())))
                        .andExpect(jsonPath("$[1].x", is(savedWidgetList.get(1).getX()), Long.class))
                        .andExpect(jsonPath("$[1].y", is(savedWidgetList.get(1).getY()), Long.class))
                        .andExpect(jsonPath("$[1].z", is(savedWidgetList.get(1).getZ()), Long.class))
                        .andExpect(jsonPath("$[1].width", is(savedWidgetList.get(1).getWidth()), Float.class))
                        .andExpect(jsonPath("$[1].height", is(savedWidgetList.get(1).getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<Widget> widgets = objectMapper.readValue(responseString, new TypeReference<List<Widget>>() {
        });

        assertThat(widgets).hasSize(2);

        assertThat(widgets.get(0).getLastModificationDate())
                .isAtSameInstantAs(savedWidgetList.get(0).getLastModificationDate());
    }
    @Test
    @Order(13)
    void testGetAllWidgetsWithInvalidAreaFilterReturnsAll() throws Exception {
        //Given three widgets has been inserted earlier with (X,Y) are (50,50), (50,100) and (100,100)
        //And Given filter missing the leftX (invalid)

        // When
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .queryParam("rightX", "100")
                        .queryParam("lowerY", "0")
                        .queryParam("higherY", "150")
                );

        // Then
        verify(widgetsService).getAllWidgets(anyInt(), anyInt(), any());
        List<Widget> savedWidgetList = new ArrayList<>(savedWidgets.values());
        MvcResult mvcResult =
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray())
                        .andExpect(jsonPath("$.*", hasSize(3)))
                        .andExpect(jsonPath("$[0].id", is(savedWidgetList.get(2).getId().toString())))
                        .andExpect(jsonPath("$[0].x", is(savedWidgetList.get(2).getX()), Long.class))
                        .andExpect(jsonPath("$[0].y", is(savedWidgetList.get(2).getY()), Long.class))
                        .andExpect(jsonPath("$[0].z", is(savedWidgetList.get(2).getZ()), Long.class))
                        .andExpect(jsonPath("$[0].width", is(savedWidgetList.get(2).getWidth()), Float.class))
                        .andExpect(jsonPath("$[0].height", is(savedWidgetList.get(2).getHeight()), Float.class))
                        .andExpect(jsonPath("$[1].id", is(savedWidgetList.get(0).getId().toString())))
                        .andExpect(jsonPath("$[1].x", is(savedWidgetList.get(0).getX()), Long.class))
                        .andExpect(jsonPath("$[1].y", is(savedWidgetList.get(0).getY()), Long.class))
                        .andExpect(jsonPath("$[1].z", is(savedWidgetList.get(0).getZ()), Long.class))
                        .andExpect(jsonPath("$[1].width", is(savedWidgetList.get(0).getWidth()), Float.class))
                        .andExpect(jsonPath("$[1].height", is(savedWidgetList.get(0).getHeight()), Float.class))
                        .andExpect(jsonPath("$[2].id", is(savedWidgetList.get(1).getId().toString())))
                        .andExpect(jsonPath("$[2].x", is(savedWidgetList.get(1).getX()), Long.class))
                        .andExpect(jsonPath("$[2].y", is(savedWidgetList.get(1).getY()), Long.class))
                        .andExpect(jsonPath("$[2].z", is(savedWidgetList.get(1).getZ()), Long.class))
                        .andExpect(jsonPath("$[2].width", is(savedWidgetList.get(1).getWidth()), Float.class))
                        .andExpect(jsonPath("$[2].height", is(savedWidgetList.get(1).getHeight()), Float.class))
                        .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<Widget> widgets = objectMapper.readValue(responseString, new TypeReference<List<Widget>>() {
        });

        assertThat(widgets).hasSize(3);

        assertThat(widgets.get(0).getLastModificationDate())
                .isAtSameInstantAs(savedWidgetList.get(2).getLastModificationDate());
    }

    @Test
    @Order(14)
    void testWidgetWithSpecifiedIndexPushesAllWidgetsWithEqualOrHigherIndexesUpwards() throws Exception {
        // Given
        WidgetRequest request = aValidWidgetRequest().z(-1L);

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        Widget saved = objectMapper.readValue(responseString, Widget.class);
        savedWidgets.put(saved.getId(), saved);

        // Then
        verify(widgetsService).addWidget(request);

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists())
                .andExpect(jsonPath("$.z", is(request.getZ()), Long.class));

        List<Widget> allWidgetsAfterLastInsert = widgetsService.getAllWidgets(anyInt(), anyInt(), any());

        allWidgetsAfterLastInsert.stream().filter(w -> w.getZ() > request.getZ()).forEach(afterWidget -> {
            Widget before = savedWidgets.get(afterWidget.getId());
            assertThat(afterWidget.getZ()).isEqualTo(before.getZ() + 1);

            assertThat(afterWidget.getLastModificationDate())
                    .isAfter(before.getLastModificationDate());
        });
    }

    @Test
    @Order(15)
    void testDeleteWidgetByIdReturns204() throws Exception {
        // Given
        Widget[] widgets = savedWidgets.values().toArray(new Widget[0]);
        Widget savedWidget = widgets[0];
        UUID id = savedWidget.getId();

        // When
        ResultActions result = mockMvc
                .perform(delete("/widgets/{id}", id));
        savedWidgets.remove(id);

        // Then
        verify(widgetsService).deleteWidget(id);

        result.andExpect(status().isNoContent());
    }

    @Test
    @Order(16)
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
    @Order(17)
    void testUpdateWidgetWillReturnsCompleteWidgetDescription() throws Exception {
        // Given
        WidgetRequest widgetRequest = new WidgetRequest().z(14L).x(2L).y(16L).height(23.4F).width(11.6F);
        Widget[] widgets = savedWidgets.values().toArray(new Widget[0]);
        Widget savedWidget = widgets[0];
        UUID id = savedWidget.getId();
        OffsetDateTime lastModificationDate = savedWidget.getLastModificationDate();

        // When
        ResultActions result = mockMvc
                .perform(
                        put("/widgets/{id}", id)
                                .content(objectMapper.writeValueAsString(widgetRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        Widget updated = objectMapper.readValue(responseString, Widget.class);
        savedWidgets.put(updated.getId(), updated);

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
    @Order(18)
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
        WidgetRequest widgetRequestWithNoZ = aValidWidgetRequest().x(100L).y(100L).z(-5L);

        // When
        ResultActions result = mockMvc
                .perform(
                        post("/widgets")
                                .content(objectMapper.writeValueAsString(widgetRequestWithNoZ))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        String responseString = result.andReturn().getResponse().getContentAsString();
        Widget saved = objectMapper.readValue(responseString, Widget.class);
        savedWidgets.put(saved.getId(), saved);
    }
}
