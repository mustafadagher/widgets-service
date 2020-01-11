package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WidgetsServiceTest {

    @InjectMocks
    private WidgetsService widgetsService;

    @Test
    void testAddWidgetReturnsFullWidgetDescription() {
        //  Given
        WidgetRequest aValidRequest = aValidWidgetRequest();

        // When
        Widget response = widgetsService.addWidget(aValidRequest);

        //  Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getLastModificationDate())
                .isNotNull()
                .isCloseToUtcNow(within(2, ChronoUnit.SECONDS));
        assertThat(response.getX()).isEqualTo(aValidRequest.getX());
        assertThat(response.getY()).isEqualTo(aValidRequest.getY());
        assertThat(response.getZ()).isEqualTo(aValidRequest.getZ());
        assertThat(response.getWidth()).isEqualTo(aValidRequest.getWidth());
        assertThat(response.getHeight()).isEqualTo(aValidRequest.getHeight());
    }
}
