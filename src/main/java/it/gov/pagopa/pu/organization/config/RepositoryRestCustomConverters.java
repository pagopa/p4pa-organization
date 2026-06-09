package it.gov.pagopa.pu.organization.config;

import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
public class RepositoryRestCustomConverters {

  private final DefaultFormattingConversionService conversionService;

  public RepositoryRestCustomConverters(DefaultFormattingConversionService conversionService) {
    this.conversionService = conversionService;
  }

  // This should be aligned with it.gov.pagopa.pu.organization.model.OrgSubUnit.OrgSubUnitId#toString
  @Bean
  public Converter<String, OrgSubUnit.OrgSubUnitId> orgSubUnitIdConverter() {
    @SuppressWarnings({"Convert2Lambda", "squid:S1604"}) // Suppressing lambda conversion warning, Spring is not able to retrieve the generic types otherwise
    Converter<String, OrgSubUnit.OrgSubUnitId> converter = new Converter<>() {
      @Override
      public OrgSubUnit.OrgSubUnitId convert(String idString) {
        String[] idTokens = idString.split("-");
        if (idTokens.length != 2) {
          throw buildInvalidOrgSubUnitIdException();
        }
        try {
          Long organizationId = Long.parseLong(idTokens[0]);
          String subUnitCode = idTokens[1];
          return new OrgSubUnit.OrgSubUnitId(
            organizationId,
            subUnitCode
          );
        } catch (Exception e) {
          throw buildInvalidOrgSubUnitIdException();
        }
      }
    };

    conversionService.addConverter(converter);

    return converter;
  }
  private static @NonNull InvalidValueException buildInvalidOrgSubUnitIdException() {
    return new InvalidValueException(ErrorCodeConstants.ERROR_CODE_ORG_SUB_UNIT_INVALID_ID, "Invalid id format for OrgSubUnit id, expected format: {organizationId}-{subUnitCode}");
  }
}
