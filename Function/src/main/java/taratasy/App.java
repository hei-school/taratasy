package taratasy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    var headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    headers.put("X-Custom-Header", "application/json");
    var response = new APIGatewayProxyResponseEvent().withHeaders(headers);

    String output = String.format("{ \"message\": \"hello world\" }");
    return response
        .withStatusCode(200)
        .withBody(output);
  }
}
