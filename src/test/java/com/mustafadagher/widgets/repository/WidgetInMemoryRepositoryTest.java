package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetAreaFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
        Optional<Widget> searchedWidget = widgetRepository.findById(validWidget.getId());

        // Then
        assertThat(savedWidget)
                .isNotNull()
                .isEqualTo(validWidget);

        assertThat(searchedWidget).containsSame(savedWidget);
    }

    @Test
    void testFindAll() {
        // Given
        Widget w1 = aValidWidget().z(1L);
        Widget w2 = aValidWidget().z(2L).height(22.3F).width(11.7F);
        Widget w3 = aValidWidget().z(6L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        // When
        List<Widget> all = widgetRepository.findAll();

        // then
        assertThat(all)
                .hasSize(3)
                .contains(w1, w2, w3);
    }

    @Test
    void testFindAllByZGreaterThanOrEqual() {
        // Given
        Widget w1 = aValidWidget().z(1L);
        Widget w2 = aValidWidget().z(2L).height(22.3F).width(11.7F);
        Widget w3 = aValidWidget().z(6L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        // When
        List<Widget> allByZGreaterThanOrEqualTwo = widgetRepository.findAllByZGreaterThanOrEqual(2L);
        // then
        assertThat(allByZGreaterThanOrEqualTwo)
                .hasSize(2)
                .contains(w2, w3);
    }

    @Test
    void testPaginationReturnsAPageOfTheSpecifiedSizeWithTheLowestZ() {
        // Given
        Widget w1 = aValidWidget().z(1L);
        Widget w2 = aValidWidget().z(2L).height(22.3F).width(11.7F);
        Widget w3 = aValidWidget().z(-1L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        // When
        List<Widget> aPageOfTwo = widgetRepository.findAllByOrderByZAsc(0, 2);

        assertThat(aPageOfTwo)
                .hasSize(2)
                .contains(w3, w1);
    }

    @Test
    void testSecondPageWithLessElements() {
        // Given
        Widget w1 = aValidWidget().z(1L);
        Widget w2 = aValidWidget().z(2L).height(22.3F).width(11.7F);
        Widget w3 = aValidWidget().z(-1L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        // When
        List<Widget> aPageOfTwo = widgetRepository.findAllByOrderByZAsc(1, 2);

        assertThat(aPageOfTwo)
                .hasSize(1)
                .contains(w2);
    }

    @Test
    void testUpdateAnExistingWidgetKeepsTheSameCount() {
        // Given
        Widget w1 = aValidWidget().z(1L);
        Widget w2 = aValidWidget().z(2L).height(22.3F).width(11.7F);
        Widget w3 = aValidWidget().z(6L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        // When
        w1.z(2L).height(14F);
        Widget updated = widgetRepository.save(w1);
        List<Widget> all = widgetRepository.findAll();

        // then
        assertThat(all)
                .hasSize(3)
                .contains(w1, w2, w3);

        assertThat(updated.getId()).isEqualTo(w1.getId());
    }

    @Test
    void testDeleteById() {
        // Given
        Widget w1 = aValidWidget().z(1L);
        Widget w2 = aValidWidget().z(2L).height(22.3F).width(11.7F);
        Widget w3 = aValidWidget().z(6L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        // When
        widgetRepository.deleteById(w1.getId());

        // Then
        List<Widget> all = widgetRepository.findAll();
        assertThat(all).hasSize(2)
                .doesNotContain(w1);
    }

    @Test
    void testReturnsWidgetsInAreaOnly() {
        // Given
        Widget w1 = aValidWidget().width(100F).height(100F).x(50L).y(50L);
        Widget w2 = aValidWidget().width(100F).height(100F).x(50L).y(100L);
        Widget w3 = aValidWidget().width(100F).height(100F).x(100L).y(100L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);
        widgetRepository.save(w3);

        WidgetAreaFilter filter = new WidgetAreaFilter(0, 100, 0, 150);

        // When
        List<Widget> filtered = widgetRepository.findAllByAreaOrderByZAsc(0, 10, filter);

        // Then
        assertThat(filtered)
                .hasSize(2)
                .contains(w1, w2);
    }

    @Test
    void testReturnsWidgetsInAreaOnlyWithWidthAndHeightThatAreNotDividableByTwo() {
        // Given
        Widget w1 = aValidWidget().width(7F).height(7F).x(6L).y(7L);
        Widget w2 = aValidWidget().width(7F).height(7F).x(7L).y(10L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);

        WidgetAreaFilter filter = new WidgetAreaFilter(3, 15, 3, 15);

        // When
        List<Widget> filtered = widgetRepository.findAllByAreaOrderByZAsc(0, 10, filter);

        // Then
        assertThat(filtered)
                .hasSize(1)
                .contains(w2);
    }

    @Test
    void testReturnsWidgetsInAreaOnlyWithNegativeIndexes() {
        // Given
        Widget w1 = aValidWidget().width(7F).height(7F).x(-6L).y(-7L);
        Widget w2 = aValidWidget().width(7F).height(7F).x(-7L).y(-10L);

        widgetRepository.save(w1);
        widgetRepository.save(w2);

        WidgetAreaFilter filter = new WidgetAreaFilter(-15, -3, -15, -3);

        // When
        List<Widget> filtered = widgetRepository.findAllByAreaOrderByZAsc(0, 10, filter);

        // Then
        assertThat(filtered)
                .hasSize(1)
                .contains(w2);
    }
}