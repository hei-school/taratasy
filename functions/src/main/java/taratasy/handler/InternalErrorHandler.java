package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.function.BiFunction;

public abstract class InternalErrorHandler
    implements BiFunction<APIGatewayProxyRequestEvent, Context, APIGatewayProxyResponseEvent> {

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent input, Context context) {
    try {
      return handleErrorCaughtRequest(input, context);
    } catch (Exception e) {
      return new APIGatewayProxyResponseEvent()
          .withStatusCode(500)
          .withBody(String.format("{ \"message\": \"%s\" }", e));
    }
  }

  protected abstract APIGatewayProxyResponseEvent handleErrorCaughtRequest(
      APIGatewayProxyRequestEvent input, Context context);
}
