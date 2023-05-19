package taratasy.handler;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import taratasy.dao.TaratasyDao;
import taratasy.dao.TaratasyDynamodb;

import java.net.URISyntaxException;

public abstract class DaoConnectedHandler extends SecuredRequestHandler {
  protected final TaratasyDao taratasyDao;

  public DaoConnectedHandler() throws URISyntaxException {
    super();
    taratasyDao = new TaratasyDao(
        DynamoDbEnhancedClient.create().table(System.getenv("TABLE_NAME"),
            TableSchema.fromImmutableClass(TaratasyDynamodb.class)));
  }

  public DaoConnectedHandler(TaratasyDao taratasyDao) throws URISyntaxException {
    super();
    this.taratasyDao = taratasyDao;
  }
}
