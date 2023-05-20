package taratasy.security.authentication;

import taratasy.security.authorization.AccessId;

public record User(Id id, Role role) {

  public static User dummyUser = new User(new User.Id("dummy"), new User.Role("dummy"));


  public record Id(String value) implements AccessId {
  }

  public record Role(String value) implements AccessId {
  }
}
