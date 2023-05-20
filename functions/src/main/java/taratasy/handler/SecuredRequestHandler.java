package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.User;
import taratasy.security.authentication.impl.iza.IzaApi;
import taratasy.security.authentication.impl.iza.IzaAuthenticator;
import taratasy.security.authorization.Authorizer;
import taratasy.security.authorization.Operation;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class SecuredRequestHandler extends InternalErrorHandler {

  private final Authenticator authenticator;
  private final Authorizer authorizer;

  public static final String AUTHORIZATION_HEADER = "authorization";

  public SecuredRequestHandler() throws URISyntaxException {
    this(new File(SecuredRequestHandler.class
        .getClassLoader()
        .getResource("authorizations.csv").toURI()));
  }

  public SecuredRequestHandler(File authorizationsFile) throws URISyntaxException {
    super();
    var izaApi = new IzaApi(new URI(System.getenv("IZA_URI")), System.getenv("IZA_API_KEY"));
    authenticator = new IzaAuthenticator(izaApi);
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
    var userId = new User.Id(input.getPathParameters().get("userId"));
    return authenticator.whois(userId);
  }

  protected abstract Operation getOperation();
}
