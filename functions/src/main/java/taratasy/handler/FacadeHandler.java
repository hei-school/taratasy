package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import taratasy.dao.TaratasyDao;
import taratasy.dao.TaratasyDynamodb;
import taratasy.security.authentication.User;
import taratasy.security.authorization.Operation;

import static taratasy.handler.DaoConnectedHandler.newDaoConnectedHandler;
import static taratasy.handler.InternalErrorHandler.newInternalErrorHandler;
import static taratasy.handler.SecuredRequestHandler.newSecuredRequestHandler;

public abstract class FacadeHandler implements AwsHandler {

  private final SecuredRequestHandler securedRequestHandler;
  private final InternalErrorHandler internalErrorHandler;
  private final DaoConnectedHandler daoConnectedHandler;

  public FacadeHandler() {
    this(new TaratasyDao(
        DynamoDbEnhancedClient.create().table(System.getenv("TABLE_NAME"),
            TableSchema.fromImmutableClass(TaratasyDynamodb.class))));
  }

  public FacadeHandler(TaratasyDao taratasyDao) {
    this.securedRequestHandler = newSecuredRequestHandler(this::handleSecuredRequest, this::getOperation);
    this.internalErrorHandler = newInternalErrorHandler(securedRequestHandler);
    this.daoConnectedHandler = newDaoConnectedHandler(internalErrorHandler, taratasyDao);
  }

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent event, Context context) {
    return daoConnectedHandler.apply(event, context);
  }

  protected abstract APIGatewayProxyResponseEvent handleSecuredRequest(
      APIGatewayProxyRequestEvent event, Context context);

  protected abstract Operation getOperation();

  protected User whoisOwner(APIGatewayProxyRequestEvent event) {
    return securedRequestHandler.whoisOwner(event);
  }

  protected TaratasyDao getTaratasyDao() {
    return daoConnectedHandler.getTaratasyDao();
  }
}
