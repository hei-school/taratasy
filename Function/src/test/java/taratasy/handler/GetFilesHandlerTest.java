package taratasy.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static taratasy.handler.GetFilesHandlerTest.MOCK_SERVER_PORT;
import static taratasy.handler.GetFilesHandlerTest.WHO_API_TOKEN_MOCK;
import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;

@SetEnvironmentVariable(key = "WHOAMI_URI", value = "http://localhost:" + MOCK_SERVER_PORT + "/whoami")
@SetEnvironmentVariable(key = "WHOIS_URI", value = "http://localhost:" + MOCK_SERVER_PORT + "/whois")
@SetEnvironmentVariable(key = "WHOIS_API_TOKEN", value = WHO_API_TOKEN_MOCK)
public class GetFilesHandlerTest {

  private WireMockServer server;
  static final int MOCK_SERVER_PORT = 1080;
  static final String WHO_API_TOKEN_MOCK = "whois-api-token";

  @BeforeEach
  void startMockServer() {
    server = new WireMockServer(MOCK_SERVER_PORT);
    server.start();
  }

  @AfterEach
  void stopMockServer() {
    server.stop();
  }

  private String setupMocksForUserId(String userId) {
    var restString = String.format("""
        { "userId": "%s", "role": "student", "unknown": "unknown" }
        """, userId);
    server.stubFor(get("/whois/" + userId)
        .withHeader(AUTHORIZATION_HEADER, equalTo(WHO_API_TOKEN_MOCK))
        .willReturn(ok().withBody(restString)));

    var userBearer = "bearer " + userId;
    server.stubFor(get("/whoami")
        .withHeader(AUTHORIZATION_HEADER, equalTo(userBearer))
        .willReturn(ok().withBody(restString)));
    return userBearer;
  }

  @Test
  public void self_can_read_self() throws MalformedURLException, URISyntaxException {
    var bemaId = "bemaId";
    var bemaBearer = setupMocksForUserId(bemaId);

    APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
        .withPath(String.format("/users/%s/files", bemaId))
        .withHeaders(Map.of(AUTHORIZATION_HEADER, bemaBearer));
    APIGatewayProxyResponseEvent result = new GetFilesHandler().apply(requestEvent, null);

    assertEquals(200, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"ok\""));
  }

  @Test
  public void bema_cannot_read_lita() throws MalformedURLException, URISyntaxException {
    var bemaId = "bemaId";
    var bemaBearer = setupMocksForUserId(bemaId);
    var litaId = "litaId";
    setupMocksForUserId(litaId);

    APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
        .withPath(String.format("/users/%s/files", litaId))
        .withHeaders(Map.of(AUTHORIZATION_HEADER, bemaBearer));
    APIGatewayProxyResponseEvent result = new GetFilesHandler().apply(requestEvent, null);

    assertEquals(403, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"forbidden\""));
  }
}
