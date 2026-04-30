package it.gov.pagopa.pu.organization.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilitiesTest {

  @Test
  void givenInvalidIbanWhenIsValidIbanThenReturnFalse() {
    String iban = "test";
    boolean result = Utilities.isValidIban(iban);
    assertFalse(result);
  }

  @Test
  void givenNullIbanWhenIsValidIbanThenReturnFalse() {
    boolean result = Utilities.isValidIban(null);
    assertFalse(result);
  }

  @Test
  void givenValidIbanWhenIsValidIbanThenReturnTrue() {
    String iban = "IT0000000000000000000000000";
    boolean result = Utilities.isValidIban(iban);
    assertTrue(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "12345", "12345abc123", "1234/abc123", "00000000001"})
  void testValidateEmptyPIVA(String piva){
    assertFalse(Utilities.isValidPIVA(piva, true));
  }

  @Test
  void testGetTraceId(){
    // Given
    String expectedResult = "TRACEID";
    setTraceId(expectedResult);

    // When
    String result = Utilities.getTraceId();

    // Then
    Assertions.assertSame(expectedResult, result);
    clearTraceIdContext();
  }

  public static void setTraceId(String traceId) {
    MDC.put("traceId", traceId);
  }
  public static void clearTraceIdContext(){
    MDC.clear();
  }

  @Test
  void testCheckImmutableField_OffsetDateTime(){
    List<String> result = new ArrayList<>();
    OffsetDateTime o1 = OffsetDateTime.now();
    OffsetDateTime o2 = o1.withOffsetSameInstant(ZoneOffset.MIN);
    Utilities.checkImmutableField("fieldName", o1, o2, result);

    Utilities.checkImmutableField("expectedDiffer", o1, o2.minusSeconds(1), result);

    Assertions.assertEquals(List.of("expectedDiffer"), result);
  }

  @Test
  void testCheckImmutableField_Comparable(){
    List<String> result = new ArrayList<>();
    BigDecimal o1 = BigDecimal.ONE;
    BigDecimal o2 = BigDecimal.valueOf(1_00, 2);
    Utilities.checkImmutableField("fieldName", o1, o2, result);

    Utilities.checkImmutableField("expectedDiffer", o1, o2.add(BigDecimal.ONE), result);

    Assertions.assertEquals(List.of("expectedDiffer"), result);
  }

  @Test
  void testCheckImmutableField_Object(){
    List<String> result = new ArrayList<>();
    String o1 = "string";
    String o2 = "string";
    Utilities.checkImmutableField("fieldName", o1, o2, result);

    Utilities.checkImmutableField("expectedDiffer", o1, o2.concat("1"), result);

    Assertions.assertEquals(List.of("expectedDiffer"), result);
  }

  @Test
  void givenPopulatedStringFieldWhenCheckBlankOrNullFieldThenEmptyList(){
    List<String> result = new ArrayList<>();
    Utilities.checkBlankOrNullField("fieldName", "populatedString", result);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void givenPopulatedObjectFieldWhenCheckBlankOrNullFieldThenEmptyList(){
    List<String> result = new ArrayList<>();
    Utilities.checkBlankOrNullField("fieldName", new Object(), result);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void givenBlankStringFieldWhenCheckBlankOrNullFieldThenEmptyList(){
    List<String> result = new ArrayList<>();
    Utilities.checkBlankOrNullField("fieldName", " ", result);

    Assertions.assertEquals(List.of("fieldName"), result);
  }

  @Test
  void givenNullObjectFieldWhenCheckBlankOrNullFieldThenEmptyList(){
    List<String> result = new ArrayList<>();
    Utilities.checkBlankOrNullField("fieldName", null, result);

    Assertions.assertEquals(List.of("fieldName"), result);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "12,true",
      "01,true",
      "00,true",
      "9,false",
      "123,false",
      "abc,false",
      "1a,false",
      "a1,false",
      " ,false",
      "'  ',false",
      "null,false"
  }, nullValues = {"null"})
  void testIsValidSegregationCode(String segregationCode, boolean expected) {
      boolean result = Utilities.isValidSegregationCode(segregationCode);
      Assertions.assertEquals(expected, result);
  }
}
