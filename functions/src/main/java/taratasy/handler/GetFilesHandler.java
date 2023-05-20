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

public class GetFilesHandler extends DaoConnectedHandler {

  private final TaratasyMapper taratasyMapper = new TaratasyMapper();

  public GetFilesHandler() throws URISyntaxException {
    super();
  }

  public GetFilesHandler(TaratasyDao taratasyDao) throws URISyntaxException {
    super(taratasyDao);
  }

  @Override
  protected APIGatewayProxyResponseEvent handleSecuredRequest(APIGatewayProxyRequestEvent input, Context context) {
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
