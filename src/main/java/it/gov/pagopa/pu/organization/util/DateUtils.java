package it.gov.pagopa.pu.organization.util;

import java.time.OffsetDateTime;

public class DateUtils {


  private DateUtils(){}

  public static boolean equals(OffsetDateTime firstDate, OffsetDateTime secondDate){
    if(firstDate == null && secondDate == null){
      return true;
    }
    return firstDate!=null && secondDate!=null
      && firstDate.isEqual(secondDate);
  }
}
