package it.gov.pagopa.pu.organization.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

/** Security configuration to use just for private services */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JWTVerifier jwtVerifier;

  public JwtAuthenticationFilter(@Value("${jwt.access-token.public-key}") String publicKey)
    throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    RSAPublicKey rsaPublicKey = CertUtils.pemPub2PublicKey(publicKey);
    Algorithm algorithm = Algorithm.RSA512(rsaPublicKey);
    jwtVerifier = JWT.require(algorithm).build();
  }

  @Override
  protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
    try {
      String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (StringUtils.hasText(authorization)) {
        String token = authorization.replace("Bearer ", "");
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String externalUserId = decodedJWT.getIssuer();
        MDC.put("externalUserId", externalUserId);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(externalUserId, token, null);
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (JWTVerificationException e) {
      log.info("An invalid accessToken has been provided: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Something gone wrong while retrieving UserInfo", e);
    }
    filterChain.doFilter(request, response);
  }
}
