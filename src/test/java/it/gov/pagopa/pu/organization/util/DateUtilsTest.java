package it.gov.pagopa.pu.organization.util;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilsTest {

  @Test
  void givenMatchingDatesWhenHaveDatesChangedThenReturnFalse() {
    OffsetDateTime firstDate = OffsetDateTime.now();
    boolean result = DateUtils.haveDatesChanged(firstDate, firstDate);
    assertFalse(result);
  }

  @Test
  void givenNoMatchingDatesWhenHaveDatesChangedThenReturnTrue() {
    OffsetDateTime firstDate = OffsetDateTime.now();
    OffsetDateTime secondDate = firstDate.plusDays(1);
    boolean result = DateUtils.haveDatesChanged(firstDate, secondDate);
    assertTrue(result);
  }

  @Test
  void givenNoSecondDateWhenHaveDatesChangedThenReturnTrue() {
    OffsetDateTime firstDate = OffsetDateTime.now();
    boolean result = DateUtils.haveDatesChanged(firstDate, null);
    assertTrue(result);
  }

  @Test
  void givenNoFirstDateWhenHaveDatesChangedThenReturnTrue() {
    OffsetDateTime secondDate = OffsetDateTime.now();
    boolean result = DateUtils.haveDatesChanged(null, secondDate);
    assertTrue(result);
  }

  @Test
  void givenNoDatesWhenHaveDatesChangedThenReturnFalse() {
    boolean result = DateUtils.haveDatesChanged(null, null);
    assertFalse(result);
  }
}
