package taratasy.security.authentication.impl.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;
import taratasy.security.authentication.WhoamiApi;
import taratasy.security.authentication.WhoisApi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;

public class RestBasedAuthenticator implements Authenticator {

  private final WhoamiApi whoamiApi;
  private final WhoisApi whoisApi;

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper om;

  public RestBasedAuthenticator(WhoamiApi whoamiApi, WhoisApi whoisApi) {
    this.whoamiApi = whoamiApi;
    this.whoisApi = whoisApi;

    om = new ObjectMapper();
    om.disable(FAIL_ON_UNKNOWN_PROPERTIES);
  }

  @Override
  public User whoami(Bearer bearer) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(whoamiApi.uri())
          .GET()
          .header(AUTHORIZATION_HEADER, bearer.value())
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var restUser = om.readValue(response.body(), RestUser.class);
      return new User(new User.Id(restUser.userId), new User.Role(restUser.role));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public User whois(User.Id userId) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(whoisApi.uri().toString() + "/" + userId.value()))
          .GET()
          .header(AUTHORIZATION_HEADER, whoisApi.apiToken().value())
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      var restUser = om.readValue(response.body(), RestUser.class);
      return new User(new User.Id(restUser.userId), new User.Role(restUser.role));
    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
