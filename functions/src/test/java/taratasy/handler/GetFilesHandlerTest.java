package taratasy.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import taratasy.dao.TaratasyDao;
import taratasy.dao.TaratasyDynamodb;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static taratasy.handler.GetFilesHandlerTest.IZA_API_KEY_MOCK;
import static taratasy.handler.GetFilesHandlerTest.MOCK_SERVER_PORT;
import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;
import static taratasy.security.authentication.impl.iza.IzaApi.API_KEY_HEADER;

@SetEnvironmentVariable(key = "IZA_URI", value = "http://localhost:" + MOCK_SERVER_PORT)
@SetEnvironmentVariable(key = "IZA_API_KEY", value = IZA_API_KEY_MOCK)
public class GetFilesHandlerTest {

  private GetFilesHandler subject;
  private DynamoDbTable<TaratasyDynamodb> dynamodbTable;

  private WireMockServer server;
  static final int MOCK_SERVER_PORT = 1080;
  static final String IZA_API_KEY_MOCK = "iza-api-key";

  @BeforeEach
  void setUp() throws URISyntaxException {
    server = new WireMockServer(MOCK_SERVER_PORT);
    server.start();

    dynamodbTable = mock(DynamoDbTable.class);
    subject = new GetFilesHandler(new TaratasyDao(dynamodbTable));
  }

  @AfterEach
  void stopMockServer() {
    server.stop();
  }

  private String setupIzaMocksForUserId(String userId) {
    var restString = String.format("""
        { "id": "%s", "role": "student", "unknown": "unknown" }
        """, userId);
    server.stubFor(get("/whois/" + userId)
        .withHeader(API_KEY_HEADER, equalTo(IZA_API_KEY_MOCK))
        .willReturn(ok().withBody(restString)));

    var userBearer = "bearer " + userId;
    server.stubFor(get("/whoami")
        .withHeader(AUTHORIZATION_HEADER, equalTo(userBearer))
        .willReturn(ok().withBody(restString)));
    return userBearer;
  }

  @Test
  public void bema_cannot_read_lita() {
    var bemaId = "bemaId";
    var bemaBearer = setupIzaMocksForUserId(bemaId);
    var litaId = "litaId";
    setupIzaMocksForUserId(litaId);

    APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
        .withPathParameters(Map.of("userId", litaId))
        .withHeaders(Map.of(AUTHORIZATION_HEADER, bemaBearer));
    APIGatewayProxyResponseEvent result = subject.apply(requestEvent, null);

    assertEquals(403, result.getStatusCode().intValue());
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"forbidden\""));
  }

  @Test
  public void self_can_read_self() {
    var bemaId = "bemaId";
    var bemaBearer = setupIzaMocksForUserId(bemaId);
    when(dynamodbTable.query(any(QueryEnhancedRequest.class))).thenReturn(
        dynamodbResponse(List.of(new TaratasyDynamodb("fileId", bemaId, "filename"))));

    APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
        .withPathParameters(Map.of("userId", bemaId))
        .withHeaders(Map.of(AUTHORIZATION_HEADER, bemaBearer));
    APIGatewayProxyResponseEvent result = subject.apply(requestEvent, null);

    assertEquals(200, result.getStatusCode().intValue());
    String content = result.getBody();
    assertEquals("""
        [{"id":"bemaId","ownerId":"fileId","name":"filename"}]""", content);
  }

  private static PageIterable<TaratasyDynamodb> dynamodbResponse(List<TaratasyDynamodb> taratasyDynamodbList) {
    return PageIterable.create((PageIterable<TaratasyDynamodb>) () -> new Iterator<>() {

      private boolean hasNext = true;

      @Override
      public boolean hasNext() {
        // We return everything in a single page for testing purpose.
        // In practice, we rather want Dynamodb to return large lists in multiple pages
        var oldHasNext = hasNext;
        hasNext = false;
        return oldHasNext;
      }

      @Override
      public Page<TaratasyDynamodb> next() {
        return Page.create(taratasyDynamodbList);
      }
    });
  }
}
