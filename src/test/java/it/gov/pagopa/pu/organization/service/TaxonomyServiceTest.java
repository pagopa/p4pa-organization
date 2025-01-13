package it.gov.pagopa.pu.organization.service;

import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxonomyServiceTest {
  @Mock
  private TaxonomyRepository taxonomyRepository;

  @InjectMocks
  private TaxonomyService taxonomyService;

  @Test
  void testGetDistinctOrganizationType() {
    // Arrange
    Sort sort = Sort.by("name");
    DistinctOrganizationTypeDTO dto1 = new DistinctOrganizationTypeDTO() {
      @Override
      public String getOrganizationType () {
        return null;
      }

      @Override
      public String getOrganizationTypeDescription () {
        return null;
      }
    };
    DistinctOrganizationTypeDTO dto2 = new DistinctOrganizationTypeDTO() {
      @Override
      public String getOrganizationType () {
        return null;
      }

      @Override
      public String getOrganizationTypeDescription () {
        return null;
      }
    };
    List<DistinctOrganizationTypeDTO> expectedList = Arrays.asList(dto1, dto2);
    Mockito.when(taxonomyRepository.findDistinctOrganizationTypes(sort)).thenReturn(expectedList);

    // Act
    List<DistinctOrganizationTypeDTO> result = taxonomyService.getDistinctOrganizationType(sort);

    // Assert
    assertEquals(expectedList, result);
    Mockito.verify(taxonomyRepository, Mockito.times(1)).findDistinctOrganizationTypes(sort);
  }
}
