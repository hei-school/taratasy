package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import taratasy.dao.TaratasyDao;
import taratasy.rest.TaratasyMapper;
import taratasy.security.authentication.User;
import taratasy.security.authorization.Operation;

import java.net.URISyntaxException;

import static taratasy.security.authorization.Operation.READ;

public class GetFilesHandler extends SecuredRequestHandler {

  private final TaratasyDao taratasyDao = new TaratasyDao();
  private final TaratasyMapper taratasyMapper = new TaratasyMapper();

  public GetFilesHandler() throws URISyntaxException {
    super();
  }

  @Override
  protected APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    User ownerUser = super.whoisOwner(input);
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withBody(taratasyMapper.toRestString(taratasyDao.findBy(ownerUser.id())));
  }

  @Override
  protected Operation getOperation() {
    return READ;
  }
}
