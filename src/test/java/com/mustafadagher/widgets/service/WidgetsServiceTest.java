package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.mustafadagher.widgets.Mocks.aValidWidget;
import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetsServiceTest {

    @InjectMocks
    private WidgetsService widgetsService;
    @Mock
    private WidgetRepository widgetRepository;

    @Captor
    private ArgumentCaptor<Widget> argumentCaptor;

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
        insertThreeWidgetsWithZIndexOneTwoAndThree(aValidRequest);

        // When
        aValidRequest.setZ(null);
        Widget response = widgetsService.addWidget(aValidRequest);

        //  Then
        assertThat(response.getZ()).isEqualTo(4);
        assertThat(widgetsService.getHighestZIndex().get()).isEqualTo(response.getZ());
    }

    @Test
    void testWidgetWithSpecifiedIndexPushesAllWidgetsWithEqualOrHigherIndexesUpwards() {
        //  Given
        WidgetRequest aValidRequest = aValidWidgetRequest();
        when(widgetRepository.save(any())).then(returnsFirstArg());

        Widget w1 = aValidWidget().z(2L);
        Widget w2 = aValidWidget().z(3L);
        when(widgetRepository.findAllByZGreaterThanOrEqual(2L)).thenReturn(Arrays.asList(w1, w2));

        // When
        aValidRequest.setZ(2L);
        Widget response = widgetsService.addWidget(aValidRequest);

        // then
        assertThat(response.getZ()).isEqualTo(2);
        verify(widgetRepository, times(3)).save(argumentCaptor.capture());
        List<Widget> allSavedOrUpdatedWidgets = argumentCaptor.getAllValues();

        assertThat(allSavedOrUpdatedWidgets)
                .hasSize(3)
                .contains(response, w1.z(3L), w2.z(4L));

    }

    private void insertThreeWidgetsWithZIndexOneTwoAndThree(WidgetRequest aValidRequest) {
        aValidRequest.setZ(1L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(2L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(3L);
        widgetsService.addWidget(aValidRequest);
    }
}
