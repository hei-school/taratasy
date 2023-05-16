package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.security.authorization.Operation;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static taratasy.security.authorization.Operation.READ;

public class GetFilesHandler extends SecuredRequestHandler {

  public GetFilesHandler() throws URISyntaxException, MalformedURLException {
    super();
  }

  @Override
  protected APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withBody("{ \"message\": \"ok\" }");
  }

  @Override
  protected Operation getOperation() {
    return READ;
  }
}
