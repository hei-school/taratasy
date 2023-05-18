package taratasy.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import taratasy.model.Taratasy;
import taratasy.security.authentication.User;

import java.util.List;

public class TaratasyMapper {

  private final ObjectMapper om = new ObjectMapper();

  public Taratasy toModel(TaratasyRest rest) {
    return new Taratasy(
        new Taratasy.Id(rest.id()),
        new User.Id(rest.ownerId()),
        rest.name());
  }

  private TaratasyRest toRest(Taratasy model) {
    return new TaratasyRest(
        model.id().value(),
        model.ownerId().value(),
        model.name());
  }

  public List<Taratasy> toModel(List<TaratasyRest> rests) {
    return rests.stream().map(this::toModel).toList();
  }

  private List<TaratasyRest> toRest(List<Taratasy> models) {
    return models.stream().map(this::toRest).toList();
  }

  public String toRestString(List<Taratasy> models) {
    try {
      return om.writeValueAsString(toRest(models));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
