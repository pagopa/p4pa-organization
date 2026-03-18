package it.gov.pagopa.pu.organization.util;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilsTest {

  @Test
  void givenMatchingDatesWhenEqualsThenReturnTrue() {
    OffsetDateTime firstDate = OffsetDateTime.now();
    boolean result = DateUtils.equals(firstDate, firstDate);
    assertTrue(result);
  }

  @Test
  void givenNoMatchingDatesWhenEqualsThenReturnFalse() {
    OffsetDateTime firstDate = OffsetDateTime.now();
    OffsetDateTime secondDate = firstDate.plusDays(1);
    boolean result = DateUtils.equals(firstDate, secondDate);
    assertFalse(result);
  }

  @Test
  void givenNoSecondDateWhenEqualsThenReturnFalse() {
    OffsetDateTime firstDate = OffsetDateTime.now();
    boolean result = DateUtils.equals(firstDate, null);
    assertFalse(result);
  }

  @Test
  void givenNoFirstDateWhenEqualsThenReturnFalse() {
    OffsetDateTime secondDate = OffsetDateTime.now();
    boolean result = DateUtils.equals(null, secondDate);
    assertFalse(result);
  }

  @Test
  void givenNoDatesWhenEqualsThenReturnTrue() {
    boolean result = DateUtils.equals(null, null);
    assertTrue(result);
  }
}
