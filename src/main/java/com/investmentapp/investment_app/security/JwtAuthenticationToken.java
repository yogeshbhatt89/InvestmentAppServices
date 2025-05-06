package com.investmentapp.investment_app.security;

import com.investmentapp.investment_app.model.User;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final User user;

  public JwtAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.user = user;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return this.user;
  }
}
