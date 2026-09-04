package it.gov.pagopa.pu.organization.util;

import java.time.ZoneId;
import java.util.TimeZone;

public class Constants {

  private Constants(){}

  public static final ZoneId ZONEID = ZoneId.of("Europe/Rome");
  public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone(ZONEID);
  public static final String DEFAULT_IUV_SYSTEM_ID = "00";

  public static final String UPDATE_AUDIT_FIELDS_SPEL = "updateDate = :#{T(java.time.LocalDateTime).now()}, " +
    "updateOperatorExternalId = :#{T(it.gov.pagopa.pu.organization.util.SecurityUtils).getCurrentUserExternalId()}, " +
    "updateTraceId = :#{T(it.gov.pagopa.pu.organization.util.Utilities).getTraceId()}";
}

