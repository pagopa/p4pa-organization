package it.gov.pagopa.pu.organization.util;

import io.micrometer.common.util.StringUtils;
import org.slf4j.MDC;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Utilities {
  private Utilities(){}

  public static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z0-9]{23,30}$");

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
    return iban != null && IBAN_PATTERN.matcher(iban).matches();
  }

  public static <T> void checkImmutableField(String fieldName, T original, T updated, List<String> modifiedFields){
    @SuppressWarnings("unchecked") // suppressing: same type due to same Generic type
    boolean fieldUpdated =
            (original instanceof OffsetDateTime o1 && updated instanceof OffsetDateTime o2)
                    ? o1.toEpochSecond() != o2.toEpochSecond()
                    : (original instanceof @SuppressWarnings("rawtypes")Comparable c1 && updated instanceof Comparable<?> c2)
                    ? c1.compareTo(c2) != 0
                    : !Objects.equals(original, updated);
    if(fieldUpdated){
      modifiedFields.add(fieldName);
    }
  }

  public static <T> void checkBlankOrNullField(String fieldName, T field, List<String> modifiedFields){
    boolean emptyOrNullField =
            (field instanceof String s)
                    ? StringUtils.isBlank(s)
                    : field == null;
    if(emptyOrNullField){
      modifiedFields.add(fieldName);
    }
  }
}
