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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.BiFunction;

public abstract class SecuredRequestHandler
    implements BiFunction<APIGatewayProxyRequestEvent, Context, APIGatewayProxyResponseEvent> {

  private final Authenticator authenticator;
  private final Authorizer authorizer;

  public SecuredRequestHandler() throws URISyntaxException, MalformedURLException {
    this(new File(SecuredRequestHandler.class
        .getClassLoader()
        .getResource("authorizations.csv").toURI()));
  }

  public SecuredRequestHandler(File authorizationsFile) throws MalformedURLException {
    authenticator = new UrlBasedAuthenticator(new URL(System.getenv("WHOAMI_AUTHENTICATOR_BASEURL")));
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
    return authenticator.apply(new Bearer(input.getHeaders().get("authorization")));
  }

  protected Target getTargetUserId(APIGatewayProxyRequestEvent input) {
    throw new RuntimeException("TODO");
  }

  protected Target getTargetUserRole(APIGatewayProxyRequestEvent input) {
    throw new RuntimeException("TODO");
  }

  protected abstract Operation getTargetOperation();

  protected abstract APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context);
}
