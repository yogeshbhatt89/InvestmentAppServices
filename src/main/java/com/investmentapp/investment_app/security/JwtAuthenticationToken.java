package com.investmentapp.investment_app.security;

import com.investmentapp.investment_app.model.User;
import java.io.Serial;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
  @Serial private static final long serialVersionUID = 1L;

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
    // No need for defensive copy here as User class now handles its own defensive copying
    return this.user;
  }
}
