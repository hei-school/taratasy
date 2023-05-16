package taratasy.security.authentication.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;
import taratasy.security.authentication.WhoamiApi;
import taratasy.security.authentication.WhoisApi;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;

public class UriBasedAuthenticator implements Authenticator {

  private final WhoamiApi whoamiApi;
  private final WhoisApi whoisApi;
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper om = new ObjectMapper();

  public UriBasedAuthenticator(WhoamiApi whoamiApi, WhoisApi whoisApi) {
    this.whoamiApi = whoamiApi;
    this.whoisApi = whoisApi;
  }

  @Override
  public User apply(Bearer bearer) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(whoamiApi.uri())
          .GET()
          .header(AUTHORIZATION_HEADER, bearer.value())
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return om.readValue(response.body(), User.class);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
