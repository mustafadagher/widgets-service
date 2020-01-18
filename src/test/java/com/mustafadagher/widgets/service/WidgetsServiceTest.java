package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.exception.WidgetNotFoundException;
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

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mustafadagher.widgets.Mocks.aValidWidget;
import static com.mustafadagher.widgets.Mocks.aValidWidgetRequest;
import static org.assertj.core.api.Assertions.*;
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

    @Test
    void testGetWidgetByIdThrowsWidgetNotFoundExceptionIfNotExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(widgetRepository.findById(id)).thenReturn(Optional.empty());

        // When

        Throwable thrown = catchThrowable(() -> widgetsService.getWidgetById(id));

        //
        assertThat(thrown).isInstanceOf(WidgetNotFoundException.class);

    }

    @Test
    void testGetAllWidgetsReturnsAllSortedByZ() {
        // Given
        givenRepositoryReturnsThreeWidgets();

        // When
        List<Widget> allWidgets = widgetsService.getAllWidgets(0, 10);

        // Then
        verify(widgetRepository).findAllByOrderByZAsc(0, 10);

        assertThat(allWidgets).hasSize(3);
    }

    @Test
    void testGetAllWidgetsReturnsEmptyListIfEmpty() {
        // Given
        when(widgetRepository.findAllByOrderByZAsc(0, 10)).thenReturn(null);

        // When
        List<Widget> allWidgets = widgetsService.getAllWidgets(0, 10);

        // Then
        verify(widgetRepository).findAllByOrderByZAsc(0, 10);

        assertThat(allWidgets).isEmpty();
    }

    @Test
    void testDeleteWidget() {
        // Given
        Widget widget = aValidWidget();
        when(widgetRepository.findById(widget.getId())).thenReturn(Optional.of(widget));

        // When

        widgetsService.deleteWidget(widget.getId());

        //
        verify(widgetRepository).deleteById(widget.getId());
    }

    @Test
    void testDeleteWidgetThrowsWidgetNotFoundExceptionIfNotExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(widgetRepository.findById(id)).thenReturn(Optional.empty());

        // When

        Throwable thrown = catchThrowable(() -> widgetsService.deleteWidget(id));

        //
        assertThat(thrown).isInstanceOf(WidgetNotFoundException.class);
    }

    @Test
    void testUpdateWidget() {
        // Given
        OffsetDateTime aMinuteAgo = OffsetDateTime.now().minus(1, ChronoUnit.MINUTES);
        Widget widget = aValidWidget().lastModificationDate(aMinuteAgo);
        when(widgetRepository.findById(widget.getId())).thenReturn(Optional.of(widget));
        when(widgetRepository.save(any())).then(returnsFirstArg());

        // When

        widgetsService.updateWidgetById(widget.getId(), aValidWidgetRequest());

        //
        verify(widgetRepository).save(argumentCaptor.capture());

        Widget toBeSavedWidget = argumentCaptor.getValue();

        assertThat(toBeSavedWidget)
                .isEqualToIgnoringGivenFields(widget, "lastModificationDate");

        assertThat(toBeSavedWidget.getLastModificationDate())
                .isAfter(widget.getLastModificationDate());
    }

    @Test
    void testUpdateWidgetThrowsWidgetNotFoundExceptionIfNotExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(widgetRepository.findById(id)).thenReturn(Optional.empty());

        // When

        Throwable thrown = catchThrowable(() -> widgetsService.updateWidgetById(id, aValidWidgetRequest()));

        //
        assertThat(thrown).isInstanceOf(WidgetNotFoundException.class);
    }

    private void insertThreeWidgetsWithZIndexOneTwoAndThree(WidgetRequest aValidRequest) {
        aValidRequest.setZ(1L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(2L);
        widgetsService.addWidget(aValidRequest);

        aValidRequest.setZ(3L);
        widgetsService.addWidget(aValidRequest);
    }

    private void givenRepositoryReturnsThreeWidgets() {
        List<Widget> widgetList = Arrays.asList(aValidWidget(), aValidWidget(), aValidWidget());
        when(widgetRepository.findAllByOrderByZAsc(0, 10)).thenReturn(widgetList);
    }
}
