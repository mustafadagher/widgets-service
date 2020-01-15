package com.mustafadagher.widgets.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.service.WidgetsService;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WidgetsApiController widgetsApiController;

    @SpyBean
    private WidgetsService widgetsService;

    @Test
    void contextLoads() {
        assertThat(widgetsApiController).isNotNull();
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

        // Then
        verify(widgetsService).addWidget(widgetRequestWithNoZ);

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastModificationDate").exists())
                .andExpect(jsonPath("$.z").exists())
                .andExpect(jsonPath("$.z", matchesRegex("^-?\\d{1,19}$"), String.class))
                .andExpect(jsonPath("$.z", IsNot.not(widgetRequestWithNoZ.getZ()), Long.class));
    }
}
