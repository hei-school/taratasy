package taratasy.model;

import taratasy.security.authentication.User;

public record Taratasy(Taratasy.Id id, User.Id ownerId, String name) {
  public record Id(String value) {
  }
}
