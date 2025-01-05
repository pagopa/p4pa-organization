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

  // public key and JWT token sample obtained through p4pa-auth test it.gov.pagopa.payhub.auth.service.AccessTokenBuilderServiceTest
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
  private static final String JWT_TOKEN_USERID = "MAPPEDUSEREXTERNALID";
  private static final String JWT_TOKEN = "eyJraWQiOiIyNWNhZDlkYi0wMDIyLTNiODctYTcwYS1mMmRhMjcyMTdjODgiLCJ0eXAiOiJhdCtKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJBUFBMSUNBVElPTl9BVURJRU5DRSIsImp0aSI6IjA2ZWZmMzhjLTZhZDEtNGU5Ni1iYmYyLTUxYWVlMTFiNzZmYyIsInN1YiI6Ik1BUFBFRFVTRVJFWFRFUk5BTElEIiwiaWF0IjoxNzM2MDgwNTMzLCJleHAiOjI3MzYwODA1MzIsIm9yZ2FuaXphdGlvbklwYUNvZGUiOiJPUkdJUEFDT0RFIn0.qfcPvKVW6GOPC-Hb4QfqEpfT1zwrZ30QRbW2RPvrAlaBdYi51ZTmy6iWIcoy7YubkkctRp7xHDgcQuMRyRzGr2S-FayTA7kHXwa0y9UOnb7FXuZn9j0G6-4qVqlH6qo2KKTuDl_HykDAEmbI0AMJXilN8cM_ZkIQXCv6mDWsQCcxglsxcw89G0U9m5cZ5n9RxaAikMp8xRssiSqoFdhA67j-Iqs9P0vC-L0YvrIuqJ8CuJxoZQX_rPh-aLAzjPswctT_yaUk2tX5XpYG_1Yo0k9Mxy7CyyUa1JbRLRWbXkfOCPDbBOMn6KkXU_2w3pj4u6sIZsWuTNGT4d8zBye8JA";

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
