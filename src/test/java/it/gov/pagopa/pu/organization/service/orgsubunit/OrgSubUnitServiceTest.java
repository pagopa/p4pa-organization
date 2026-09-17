package it.gov.pagopa.pu.organization.service.orgsubunit;

import it.gov.pagopa.pu.organization.dto.OrgAndSubUnitDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitServiceTest {
  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private OrganizationRepository organizationRepositoryMock;
  @Mock
  private OrgSubUnitRepository orgSubUnitRepositoryMock;
  @InjectMocks
  private OrgSubUnitService orgSubUnitService;


  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(organizationRepositoryMock, orgSubUnitRepositoryMock);
  }

  @Test
  void givenPdndServiceTypeWhenGetOrgSubUnitWithNoServiceTypeThenOk() {
    Long organizationId = 1L;

    OrgAndSubUnitDTO organization = podamFactory.manufacturePojo(OrgAndSubUnitDTO.class);
    OrgAndSubUnitDTO subUnit = podamFactory.manufacturePojo(OrgAndSubUnitDTO.class);

    List<OrgAndSubUnitDTO> expectedResult = List.of(organization, subUnit);

    when(organizationRepositoryMock.findOrgWithNoServiceType(organizationId, PdndServiceType.SEND))
      .thenReturn(Optional.of(organization));
    when(orgSubUnitRepositoryMock.findSubUnitsWithNoServiceType(organizationId, PdndServiceType.SEND))
      .thenReturn(List.of(subUnit));

    List<OrgAndSubUnitDTO> result = orgSubUnitService.getOrgSubUnitWithNoServiceType(organizationId, PdndServiceType.SEND);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

}
