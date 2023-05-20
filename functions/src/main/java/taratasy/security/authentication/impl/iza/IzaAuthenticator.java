package taratasy.security.authentication.impl.iza;

import org.crac.Context;
import org.crac.Resource;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;

import static org.crac.Core.getGlobalContext;

public class IzaAuthenticator implements Authenticator, Resource {
  private final IzaApi izaApi;

  public IzaAuthenticator(IzaApi izaApi) {
    this.izaApi = izaApi;
    getGlobalContext().register(this);
  }

  @Override
  public User whoami(Bearer bearer) {
    return izaApi.whoami(bearer);
  }

  @Override
  public User whois(User.Id userId) {
    return izaApi.whois(userId);
  }

  @Override
  public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
    try {
      whoami(new Bearer("dummy"));
    } catch (Exception e) {

    } finally {
      System.out.println("IzaAuth warmed");
    }
  }

  @Override
  public void afterRestore(Context<? extends Resource> context) throws Exception {

  }
}
