package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mustafadagher.widgets.Mocks.aValidWidget;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WidgetInMemoryRepositoryTest {

    @InjectMocks
    private WidgetInMemoryRepository widgetRepository;

    @Test
    void testSavePersistsToInMemCacheAndReturnsTheSameValueBySearch() {
        // Given
        Widget validWidget = aValidWidget();

        // When
        Widget savedWidget = widgetRepository.save(validWidget);
        Widget searchedWidget = widgetRepository.findById(validWidget.getId());

        // Then
        assertThat(savedWidget)
                .isNotNull()
                .isEqualTo(validWidget);

        assertThat(searchedWidget)
                .isNotNull()
                .isEqualTo(savedWidget);
    }
}