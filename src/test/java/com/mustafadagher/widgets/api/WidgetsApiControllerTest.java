package com.mustafadagher.widgets.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafadagher.widgets.model.WidgetRequest;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class WidgetsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WidgetsApiController widgetsApiController;

    @Test
    void contextLoads() {
        assertNotNull(widgetsApiController);
    }

    @Test
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

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists())
                .andExpect(jsonPath("$.x", is(widgetRequest.getX()), Long.class))
                .andExpect(jsonPath("$.y", is(widgetRequest.getY()), Long.class))
                .andExpect(jsonPath("$.z", is(widgetRequest.getZ()), Long.class))
                .andExpect(jsonPath("$.width", is(widgetRequest.getWidth()), Float.class))
                .andExpect(jsonPath("$.height", is(widgetRequest.getHeight()), Float.class));
    }

    private WidgetRequest aValidWidgetRequest() {
        return new WidgetRequest()
                .height(1.5F).width(1.5F)
                .x(0L).y(0L).z(-1L);
    }

}