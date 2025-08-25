package it.gov.pagopa.pu.organization.util;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;

public class UtilitiesTest {

  @Test
  void testIbanInvalid(){
    assertFalse(Utilities.isValidIban("test"));
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
}
