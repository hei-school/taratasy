package taratasy.security.authentication;

public interface Authenticator {
  User whoami(Bearer bearer);

  User whois(User.Id userId);
}
