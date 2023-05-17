package taratasy.dao;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import taratasy.model.Taratasy;
import taratasy.security.authentication.User;

import java.util.List;

public class TaratasyDao {
  protected DynamoDbEnhancedClient dynamodbClient = DynamoDbEnhancedClient.create();

  public Taratasy findBy(User.Id userId) {
    return null;
  }

  public Taratasy findBy(User.Id userId, Taratasy.Id taratasyId) {
    return null;
  }

  public List<Taratasy> create(User.Id userId, List<Taratasy> taratasyList) {
    return null;
  }

  public List<Taratasy> update(User.Id userId, List<Taratasy> taratasyList) {
    return null;
  }
}
