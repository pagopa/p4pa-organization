package it.gov.pagopa.pu.organization.util;

import org.slf4j.MDC;

public class Utilities {
  private Utilities(){}

  public static final int IBAN_LENGTH = 27;

  public static String getTraceId(){
    return MDC.get("traceId");
  }

  public static boolean isValidPIVA(String pi, boolean isOrgPIvaCheckEnabled) {
    int i;
    int c;
    int s;
    if (pi.isEmpty())
      return false;
    if (pi.length() != 11)
      return false;
    for (i = 0; i < 11; i++) {
      if (pi.charAt(i) < '0' || pi.charAt(i) > '9')
        return false;
    }
    if(isOrgPIvaCheckEnabled) {
      s = 0;
      for (i = 0; i <= 9; i += 2)
        s += pi.charAt(i) - '0';
      for (i = 1; i <= 9; i += 2) {
        c = 2 * (pi.charAt(i) - '0');
        if (c > 9)
          c = c - 9;
        s += c;
      }
      return (10 - s % 10) % 10 == pi.charAt(10) - '0';
    }
    return true;
  }

  public static boolean isValidIban(String iban) {
    return iban != null && iban.length() == IBAN_LENGTH;
  }
}
