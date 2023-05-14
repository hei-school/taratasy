package taratasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    var headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    headers.put("X-Custom-Header", "application/json");
    var response = new APIGatewayProxyResponseEvent().withHeaders(headers);

    return response
        .withStatusCode(200)
        .withBody("{ \"message\": \"hello world\" }");
  }
}
