package it.gov.pagopa.pu.organization.util;

import java.time.OffsetDateTime;

public class DateUtils {


  private DateUtils(){}

  public static boolean haveDatesChanged(OffsetDateTime firstDate, OffsetDateTime secondDate){
    if(firstDate == null && secondDate == null){
      return false;
    }
    return (firstDate==null || secondDate==null)
      || !firstDate.isEqual(secondDate);
  }
}
