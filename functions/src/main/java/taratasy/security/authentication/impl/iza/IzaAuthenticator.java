package taratasy.security.authentication.impl.iza;

import lombok.AllArgsConstructor;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;

@AllArgsConstructor
public class IzaAuthenticator implements Authenticator {
  private final IzaApi izaApi;

  @Override
  public User whoami(Bearer bearer) {
    return izaApi.whoami(bearer);
  }

  @Override
  public User whois(User.Id userId) {
    return izaApi.whois(userId);
  }
}
