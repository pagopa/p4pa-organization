package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PagedPdndServiceView;
import it.gov.pagopa.pu.organization.model.view.PdndServiceView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.pu.organization.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.pu.organization.util.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdndServiceViewMapperTest {

  private PdndServiceViewMapper pdndServiceViewMapper;

  @BeforeEach
  void setUp() {
    pdndServiceViewMapper = new PdndServiceViewMapper();
  }

  @Test
  void givenPagedPdndServiceViewThenOk() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 5);
    PdndServiceView sampleView = new PdndServiceView(); // Assicurati che il costruttore/builder esista
    List<PdndServiceView> expectedContent = List.of(sampleView);
    Page<PdndServiceView> pagePdndServiceView = new PageImpl<>(expectedContent, pageable, 1);

    PagedPdndServiceView expectedPagedPdndServiceView = new PagedPdndServiceView();
    expectedPagedPdndServiceView.setContent(expectedContent);
    expectedPagedPdndServiceView.setTotalPages(1L);
    expectedPagedPdndServiceView.setSize(5L);
    expectedPagedPdndServiceView.setNumber(0L);
    expectedPagedPdndServiceView.setTotalElements(1L);

    // Act
    PagedPdndServiceView result = pdndServiceViewMapper.mapToPagedPdndServiceView(pagePdndServiceView);

    // Assert
    assertNotNull(result);
    checkNotNullFields(result);
    reflectionEqualsByName(expectedPagedPdndServiceView, result);
    assertEquals(expectedContent, result.getContent());
  }

  @Test
  void givenEmptyPagedPdndServiceViewThenReturnEmptyPagedResult() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    Page<PdndServiceView> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    // Act
    PagedPdndServiceView result = pdndServiceViewMapper.mapToPagedPdndServiceView(emptyPage);

    // Assert
    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    assertEquals(0L, result.getTotalElements());
    assertEquals(0L, result.getTotalPages());
    assertEquals(10L, result.getSize());
    assertEquals(0L, result.getNumber());
  }

  @Test
  void givenNullPageThenReturnEmptyPagedPdndServiceView() {
    // Act
    PagedPdndServiceView result = pdndServiceViewMapper.mapToPagedPdndServiceView(null);

    // Assert
    assertNotNull(result);
    assertTrue(result.getContent() == null || result.getContent().isEmpty());
  }

}
