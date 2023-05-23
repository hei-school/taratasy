package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public abstract class InternalErrorHandler implements AwsHandler {

  public static InternalErrorHandler newInternalErrorHandler(AwsHandler awwHandler) {
    return new InternalErrorHandler() {
      @Override
      protected APIGatewayProxyResponseEvent handleErrorCaughtRequest(
          APIGatewayProxyRequestEvent event, Context context) {
        return awwHandler.apply(event, context);
      }
    };
  }

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent event, Context context) {
    try {
      return handleErrorCaughtRequest(event, context);
    } catch (Exception e) {
      return new APIGatewayProxyResponseEvent()
          .withStatusCode(500)
          .withBody(String.format("{ \"message\": \"%s\" }", e));
    }
  }

  protected abstract APIGatewayProxyResponseEvent handleErrorCaughtRequest(
      APIGatewayProxyRequestEvent input, Context context);
}
