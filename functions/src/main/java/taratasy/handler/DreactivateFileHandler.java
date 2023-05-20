package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.security.authorization.Operation;

import java.net.URISyntaxException;

import static taratasy.security.authorization.Operation.DREACTIVATE;

public class DreactivateFileHandler extends SecuredRequestHandler {

  public DreactivateFileHandler() throws URISyntaxException {
    super();
  }

  @Override
  protected APIGatewayProxyResponseEvent handleSecuredRequest(APIGatewayProxyRequestEvent input, Context context) {
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withBody("{ \"message\": \"ok\" }");
  }

  @Override
  protected Operation getOperation() {
    return DREACTIVATE;
  }
}
