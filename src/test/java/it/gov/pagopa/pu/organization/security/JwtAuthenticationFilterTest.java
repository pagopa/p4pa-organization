package it.gov.pagopa.pu.organization.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  private static final String PUBLIC_KEY = """
    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2ovm/rd3g69dq9PisinQ
    6mWy8ZttT8D+GKXCsHZycsGnN7b74TPyYy+4+h+9cgJeizp8RDRrufHjiBrqi/2r
    eOk/rD7ZHbpfQvHK8MYfgIVdtTxYMX/GGdOrX6/5TV2b8e2aCG6GmxF0UuEvxY9o
    TmcZUxnIeDtl/ixz4DQ754eS363qWfEA92opW+jcYzr07sbQtR86e+Z/s/CUeX6W
    1PHNvBqdlAgp2ecr/1DOLq1D9hEANBPSwbt+FM6FNe4vLphi7GTwiB0yaAuy+jE8
    odND6HPvvvmgbK1/2qTHn/HJjWUm11LUC73BszR32BKbdEEhxPQnnwswVekWzPi1
    IwIDAQAB
    -----END PUBLIC KEY-----
    """;

  private static final String JWT_TOKEN_USERID = "MAPPEDEXTERNALUSERID";
  private static final String JWT_TOKEN = "eyJraWQiOiIyNWNhZDlkYi0wMDIyLTNiODctYTcwYS1mMmRhMjcyMTdjODgiLCJ0eXAiOiJhdCtKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJNQVBQRURFWFRFUk5BTFVTRVJJRCIsImp0aSI6IjBlNTM5NzRlLTczMTktNGU4Yi04OTNhLTEzYWY0ZGY2MjA2ZCIsImlhdCI6MTczNjAwODAwMCwiZXhwIjoyNzM2MDA3OTk5fQ.e1eO-qN2wxgxuA90nFgO2vwPFbSkIJcvha8xu5spv6AxMB0r9o-WhrvxBGyn8DB9VUY-alQ6N7L0WNHf5exOi_Wc1ioz4bBIiI-u6_GeFJdMkUnrAiYNXXaIUKFonzSnDv5tzyqn9XJjS3-C-QLCjo0OvYfU19sOcMT0pRWNLYXIY5YatCHH1B7ICE6GKCARVvxse5i4F9sGe2H2S_glbZUuwOm0V9-iGMX7OYK9rgW7FxV1WTvuuCT2REl1Bd6FyQ5MqIk8gMyzG9pGX1_fAtWKBqnnYlRDbfPDjVyMOJy4yDIegY6leJlZf9fbcLdlCHGLkn6bSJ6MLgRqy3ZQMA";

  @Mock
  private HttpServletRequest httpServletRequestMock;
  @Mock
  private HttpServletResponse httpServletResponseMock;
  @Mock
  private FilterChain filterChainMock;

  private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(PUBLIC_KEY);

  JwtAuthenticationFilterTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    SecurityContextHolder.clearContext();

    Mockito.verifyNoMoreInteractions(
      httpServletRequestMock,
      httpServletResponseMock,
      filterChainMock
    );
  }

  @Test
  void givenNotAuthHeaderWhenFilterThenSkipValidation() throws ServletException, IOException {
    // Given
    Mockito.when(httpServletRequestMock.getHeader(HttpHeaders.AUTHORIZATION))
      .thenReturn(null);

    // When
    filter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

    // Then
    Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

    Mockito.verify(filterChainMock)
      .doFilter(httpServletRequestMock, httpServletResponseMock);
  }


  @Test
  void givenNotValidTokenWhenFilterThenNotSecurityContextSetup() throws ServletException, IOException {
    // Given
    Mockito.when(httpServletRequestMock.getHeader(HttpHeaders.AUTHORIZATION))
      .thenReturn("INVALIDTOKEN");

    // When
    filter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

    // Then
    Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

    Mockito.verify(filterChainMock)
      .doFilter(httpServletRequestMock, httpServletResponseMock);
  }

  @Test
  void givenValidTokenWhenFilterThenSecurityContextConfigured() throws ServletException, IOException {
    // Given
    Mockito.when(httpServletRequestMock.getHeader(HttpHeaders.AUTHORIZATION))
      .thenReturn(JWT_TOKEN);

    // When
    filter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

    // Then
    Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    Assertions.assertEquals(JWT_TOKEN_USERID, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    Assertions.assertSame(JWT_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials());

    Assertions.assertEquals(JWT_TOKEN_USERID, MDC.get("externalUserId"));

    Mockito.verify(filterChainMock)
      .doFilter(httpServletRequestMock, httpServletResponseMock);
  }
}
