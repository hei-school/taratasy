package taratasy.dao;

import taratasy.model.Taratasy;
import taratasy.security.authentication.User;

public class TaratasyMapper {
  public Taratasy toModel(TaratasyDynamodb model) {
    return new Taratasy(
        new Taratasy.Id(model.getFileId()),
        new User.Id(model.getOwnerId()),
        model.getName());
  }
}
