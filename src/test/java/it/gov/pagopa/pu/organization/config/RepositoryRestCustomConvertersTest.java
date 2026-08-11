package it.gov.pagopa.pu.organization.config;

import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RepositoryRestCustomConvertersTest {

  @Mock
  private DefaultFormattingConversionService conversionServiceMock;

  private Converter<String, OrgSubUnit.OrgSubUnitId> orgSubUnitIdConverter;

  @BeforeEach
  void init() {
    RepositoryRestCustomConverters repositoryRestCustomConverters = new RepositoryRestCustomConverters(conversionServiceMock);

    orgSubUnitIdConverter = repositoryRestCustomConverters.orgSubUnitIdConverter();
    verify(conversionServiceMock).addConverter(orgSubUnitIdConverter);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(conversionServiceMock);
  }

  //region debtPositionTypeOrgBalanceCostIdConverter test
  @Test
  void givenInvalidIdWhenOrgSubUnitIdConverterThenInvalidValueException() {
    InvalidValueException resultException = Assertions.assertThrows(InvalidValueException.class, () -> orgSubUnitIdConverter.convert("INVALID"));

    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_ORG_SUB_UNIT_INVALID_ID, resultException.getCode());
  }

  @Test
  void givenExceptionWhileParsingIdWhenOrgSubUnitIdConverterThenInvalidValueException() {
    InvalidValueException resultException = Assertions.assertThrows(InvalidValueException.class, () -> orgSubUnitIdConverter.convert("X-AOO"));

    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_ORG_SUB_UNIT_INVALID_ID, resultException.getCode());
  }


  @Test
  void whenOrgSubUnitIdConverterThenOk() {
    String idString = "1-TEST";
    OrgSubUnit.OrgSubUnitId result = orgSubUnitIdConverter.convert(idString);

    Assertions.assertEquals(
      new OrgSubUnit.OrgSubUnitId(1L, "TEST"),
      result
    );

    Assertions.assertEquals(idString, result.toString());
  }
//endregion
}
