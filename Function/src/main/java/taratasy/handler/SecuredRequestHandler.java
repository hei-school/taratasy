package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.security.authentication.Authenticator;
import taratasy.security.authentication.Bearer;
import taratasy.security.authentication.Whoami;
import taratasy.security.authentication.impl.UrlBasedAuthenticator;
import taratasy.security.authorization.Authorizer;
import taratasy.security.authorization.Operation;
import taratasy.security.authorization.Principal;
import taratasy.security.authorization.Target;

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
    authenticator = new UrlBasedAuthenticator(new URI(System.getenv("WHOAMI_AUTHENTICATOR_BASEURL")));
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
    Target targetUserId = getTargetUserId(input);
    Target targetUserRole = getTargetUserRole(input);
    Operation operation = getTargetOperation();
    return authorizer.isAuthorized(new Principal(whoami.userId()), targetUserId, operation) ||
        authorizer.isAuthorized(new Principal(whoami.role()), targetUserId, operation) ||
        authorizer.isAuthorized(new Principal(whoami.userId()), targetUserRole, operation) ||
        authorizer.isAuthorized(new Principal(whoami.role()), targetUserRole, operation);
  }

  protected Whoami whoami(APIGatewayProxyRequestEvent input) {
    return authenticator.apply(new Bearer(input.getHeaders().get(AUTHORIZATION_HEADER)));
  }

  protected Target getTargetUserId(APIGatewayProxyRequestEvent input) {
    throw new RuntimeException("TODO");
  }

  protected Target getTargetUserRole(APIGatewayProxyRequestEvent input) {
    throw new RuntimeException("TODO");
  }

  protected abstract Operation getTargetOperation();
}
