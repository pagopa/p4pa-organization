package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PagedPdndClientView;
import it.gov.pagopa.pu.organization.model.view.PdndClientView;
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
class PdndClientViewMapperTest {

  private PdndClientViewMapper pdndClientViewMapper;

  @BeforeEach
  void setUp() {
    pdndClientViewMapper = new PdndClientViewMapper();
  }

  @Test
  void givenPagedPdndClientViewThenOk() {
    Pageable pageable = PageRequest.of(0, 5);
    PdndClientView pdndClientView = new PdndClientView();
    List<PdndClientView> expectedContent = List.of(pdndClientView);
    Page<PdndClientView> pagePdndClientView = new PageImpl<>(expectedContent, pageable, 1);

    PagedPdndClientView expectedPagedPdndClientView = new PagedPdndClientView();
    expectedPagedPdndClientView.setContent(expectedContent);
    expectedPagedPdndClientView.setTotalPages(1L);
    expectedPagedPdndClientView.setSize(5L);
    expectedPagedPdndClientView.setNumber(0L);
    expectedPagedPdndClientView.setTotalElements(1L);

    PagedPdndClientView result = pdndClientViewMapper.mapToPagedPdndClientView(pagePdndClientView);

    assertNotNull(result);
    checkNotNullFields(result);
    reflectionEqualsByName(expectedPagedPdndClientView, result);
    assertEquals(expectedContent, result.getContent());
  }

  @Test
  void givenEmptyPagedPdndClientViewThenReturnEmptyPagedResult() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<PdndClientView> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    PagedPdndClientView result = pdndClientViewMapper.mapToPagedPdndClientView(emptyPage);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    assertEquals(0L, result.getTotalElements());
    assertEquals(0L, result.getTotalPages());
    assertEquals(10L, result.getSize());
    assertEquals(0L, result.getNumber());
  }

  @Test
  void givenNullPageThenReturnEmptyPagedPdndClientView() {
    PagedPdndClientView result = pdndClientViewMapper.mapToPagedPdndClientView(null);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
  }

}
