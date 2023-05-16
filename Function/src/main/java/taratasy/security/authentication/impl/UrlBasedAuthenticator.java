package taratasy.security.authentication.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.Whoami;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;

public class UrlBasedAuthenticator implements Authenticator {

  private final URI authenticatorApiUri;
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper om = new ObjectMapper();

  public UrlBasedAuthenticator(URI authenticatorApiUri) {
    this.authenticatorApiUri = authenticatorApiUri;
  }

  @Override
  public Whoami apply(Bearer bearer) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(authenticatorApiUri.toString()))
          .GET()
          .header(AUTHORIZATION_HEADER, bearer.value())
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return om.readValue(response.body(), Whoami.class);
    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
