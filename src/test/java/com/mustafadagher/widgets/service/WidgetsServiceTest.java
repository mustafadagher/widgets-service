package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;

import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WidgetsServiceTest {

    @InjectMocks
    private WidgetsService widgetsService;
    @Mock
    private WidgetRepository widgetRepository;

    @Test
    void testAddWidgetReturnsFullWidgetDescription() {
        //  Given
        WidgetRequest aValidRequest = aValidWidgetRequest();
        when(widgetRepository.save(any())).then(returnsFirstArg());

        // When
        Widget response = widgetsService.addWidget(aValidRequest);

        //  Then
        verify(widgetRepository).save(response);

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

    @Test
    void testIfZNotSpecifiedWidgetIsSentToForeground() {
        //  Given
        WidgetRequest aValidRequest = aValidWidgetRequest().z(null);
        when(widgetRepository.save(any())).then(returnsFirstArg());
        long highestZIndexValueBeforeInsert = widgetsService.getHighestZIndex().get();

        // When
        Widget response = widgetsService.addWidget(aValidRequest);

        //  Then
        verify(widgetRepository).save(response);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getLastModificationDate())
                .isNotNull()
                .isCloseToUtcNow(within(2, ChronoUnit.SECONDS));
        assertThat(response.getZ()).isEqualTo(highestZIndexValueBeforeInsert + 1);
    }

    @Test
    void testIfZNotSpecifiedWidgetIsSentToForegroundMultipleInserts() {
        //  Given
        WidgetRequest aValidRequest = aValidWidgetRequest();
        when(widgetRepository.save(any())).then(returnsFirstArg());

        // When
        aValidRequest.setZ(1L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(2L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(3L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(null);
        Widget response = widgetsService.addWidget(aValidRequest);

        //  Then
        assertThat(response.getZ()).isEqualTo(4);
        assertThat(widgetsService.getHighestZIndex().get()).isEqualTo(response.getZ());
    }
}
