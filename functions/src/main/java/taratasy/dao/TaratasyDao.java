package taratasy.dao;

import org.crac.Context;
import org.crac.Resource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import taratasy.model.Taratasy;
import taratasy.security.authentication.User;

import java.util.List;

import static org.crac.Core.getGlobalContext;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;
import static taratasy.security.authentication.User.dummyUser;

public class TaratasyDao implements Resource {
  private final DynamoDbTable<TaratasyDynamodb> dynamodbTable;
  private final TaratasyMapper taratasyMapper;

  public TaratasyDao(DynamoDbTable<TaratasyDynamodb> dynamodbTable) {
    this.dynamodbTable = dynamodbTable;
    this.taratasyMapper = new TaratasyMapper();

    getGlobalContext().register(this);
  }

  public List<Taratasy> findBy(User.Id userId) {
    var keyEqual = keyEqualTo(b -> b.partitionValue(userId.value()));
    var tableQuery = QueryEnhancedRequest.builder()
        .queryConditional(keyEqual)
        .build();
    PageIterable<TaratasyDynamodb> pagedResults = dynamodbTable.query(tableQuery);
    return pagedResults.items().stream()
        .map(taratasyMapper::toModel)
        .toList();
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

  @Override
  public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
    findBy(dummyUser.id());
  }

  @Override
  public void afterRestore(Context<? extends Resource> context) throws Exception {

  }
}
