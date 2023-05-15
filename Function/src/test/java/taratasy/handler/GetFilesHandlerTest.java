package taratasy.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetFilesHandlerTest {
  @Disabled //TODO
  @Test
  public void hello() throws MalformedURLException, URISyntaxException {
    GetFilesHandler app = new GetFilesHandler();

    APIGatewayProxyResponseEvent result = app.handleRequest(null, null);

    assertEquals(200, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"ok\""));
  }
}
