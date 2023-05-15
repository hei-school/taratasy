package taratasy.security.authentication.impl;

import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.Whoami;

import java.net.URL;

public class UrlBasedAuthenticator implements Authenticator {

  private final URL authenticatorApiUrl;

  public UrlBasedAuthenticator(URL authenticatorApiUrl) {
    this.authenticatorApiUrl = authenticatorApiUrl;
  }

  @Override
  public Whoami apply(Bearer bearer) {
    throw new RuntimeException("TODO");
  }
}
