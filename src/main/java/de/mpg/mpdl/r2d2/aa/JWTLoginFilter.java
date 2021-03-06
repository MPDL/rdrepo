package de.mpg.mpdl.r2d2.aa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.aa.model.UserLogin;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

/**
 * This class controls the login process via Username and Password. When successful, it creates a
 * JWT token and returns it to the user.
 * 
 * @author haarlae1
 *
 */
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

  //TODO change this!!!!!
  public static final String SECRET = "SecretKeyToGenJWTs";
  public static final long EXPIRATION_TIME = 864_000_000; // 10 days
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
  public static final String REVIEW_TOKEN_PARAM_NAME = "reviewToken";

  private ObjectMapper mapper;

  private AuthenticationManager authenticationManager;

  public JWTLoginFilter(AuthenticationManager authenticationManager, ObjectMapper mapper) {
    this.authenticationManager = authenticationManager;
    this.mapper = mapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
    try {
      UserLogin creds = new ObjectMapper().readValue(req.getInputStream(), UserLogin.class);

      return authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), new ArrayList<>()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
      throws IOException, ServletException {

    UserAccount ua = ((R2D2Principal) auth.getPrincipal()).getUserAccount();

    //Set token in response header
    String token = JWT.create().withSubject(((User) auth.getPrincipal()).getUsername()).withClaim("user_id", ua.getId().toString())
        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(Algorithm.HMAC512(SECRET.getBytes()));
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);


    //Set user json in response body
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    res.getWriter().write(mapper.writeValueAsString(ua));

  }

}
