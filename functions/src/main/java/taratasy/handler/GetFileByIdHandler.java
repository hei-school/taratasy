package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.security.authorization.Operation;

import static taratasy.security.authorization.Operation.READ;

public class GetFileByIdHandler extends FacadeHandler {

  @Override
  protected APIGatewayProxyResponseEvent handleSecuredRequest(APIGatewayProxyRequestEvent event, Context context) {
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withBody("{ \"message\": \"ok\" }");
  }

  @Override
  protected Operation getOperation() {
    return READ;
  }
}
