package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.security.authentication.ApiToken;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;
import taratasy.security.authentication.WhoamiApi;
import taratasy.security.authentication.WhoisApi;
import taratasy.security.authentication.impl.rest.RestBasedAuthenticator;
import taratasy.security.authorization.Authorizer;
import taratasy.security.authorization.Operation;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public abstract class SecuredRequestHandler extends InternalErrorHandler {

  private final Authenticator authenticator;
  private final Authorizer authorizer;

  public static final String AUTHORIZATION_HEADER = "authorization";

  private static final Pattern PATH_WITH_USERID_PATTERN = Pattern.compile("/users/(?<userId>.*)/.*");

  public SecuredRequestHandler() throws URISyntaxException {
    this(new File(SecuredRequestHandler.class
        .getClassLoader()
        .getResource("authorizations.csv").toURI()));
  }

  public SecuredRequestHandler(File authorizationsFile) throws URISyntaxException {
    super();
    authenticator = new RestBasedAuthenticator(
        new WhoamiApi(new URI(System.getenv("WHOAMI_URI"))),
        new WhoisApi(new URI(System.getenv("WHOIS_URI")), new ApiToken(System.getenv("WHOIS_API_TOKEN"))));
    authorizer = new Authorizer(authorizationsFile);
  }

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent input, Context context) {
    if (!isAuthorized(input)) {
      return new APIGatewayProxyResponseEvent()
          .withStatusCode(403)
          .withBody("{ \"message\": \"forbidden\" }");
    }
    return handleRequest(input, context);
  }

  private boolean isAuthorized(APIGatewayProxyRequestEvent input) {
    var whoami = whoami(input);
    var ownerUser = whoisOwner(input);
    Operation operation = getOperation();
    return authorizer.isAuthorized(whoami, ownerUser, operation);
  }

  protected User whoami(APIGatewayProxyRequestEvent input) {
    return authenticator.whoami(new Bearer(input.getHeaders().get(AUTHORIZATION_HEADER)));
  }

  protected User whoisOwner(APIGatewayProxyRequestEvent input) {
    var path = input.getPath();
    var userIdMatcher = PATH_WITH_USERID_PATTERN.matcher(path);
    if (userIdMatcher.find()) {
      return authenticator.whois(new User.Id(userIdMatcher.group("userId")));
    }
    throw new RuntimeException(String.format("path=%s does not match pattern=%s", path, PATH_WITH_USERID_PATTERN));
  }

  protected abstract Operation getOperation();
}
