package taratasy.security.authentication.impl.iza;

import com.fasterxml.jackson.databind.ObjectMapper;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;

public class IzaApi {
  private final URI uri;
  private final String apiKey;
  public static final String API_KEY_HEADER = "x-api-key";

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper om;

  public IzaApi(URI uri, String apiKey) {
    this.uri = uri;
    this.apiKey = apiKey;

    om = new ObjectMapper();
    om.disable(FAIL_ON_UNKNOWN_PROPERTIES);
  }

  public User whoami(Bearer bearer) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(uri.toString() + "/whoami"))
          .GET()
          .header(AUTHORIZATION_HEADER, bearer.value())
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var userRest = om.readValue(response.body(), UserIza.class);
      return new User(new User.Id(userRest.id()), new User.Role(userRest.role()));
    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public User whois(User.Id userId) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(uri + "/whois/" + userId.value()))
          .GET()
          .header(API_KEY_HEADER, apiKey)
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var userRest = om.readValue(response.body(), UserIza.class);
      return new User(new User.Id(userRest.id()), new User.Role(userRest.role()));
    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
