package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import taratasy.dao.TaratasyDao;

@AllArgsConstructor
public abstract class DaoConnectedHandler implements AwsHandler {
  @Getter
  private final TaratasyDao taratasyDao;

  public static DaoConnectedHandler newDaoConnectedHandler(AwsHandler handler, TaratasyDao dao) {
    return new DaoConnectedHandler(dao) {
      @Override
      public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent event, Context context) {
        return handler.apply(event, context);
      }
    };
  }
}
