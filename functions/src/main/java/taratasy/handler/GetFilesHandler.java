package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.crac.Resource;
import taratasy.dao.TaratasyDao;
import taratasy.rest.TaratasyMapper;
import taratasy.security.authentication.User;
import taratasy.security.authorization.Operation;

import java.net.URISyntaxException;
import java.util.Map;

import static org.crac.Core.getGlobalContext;
import static taratasy.security.authorization.Operation.READ;

public class GetFilesHandler extends DaoConnectedHandler implements Resource {

  private final TaratasyMapper taratasyMapper = new TaratasyMapper();

  public GetFilesHandler() throws URISyntaxException {
    super();
    getGlobalContext().register(this);
  }

  public GetFilesHandler(TaratasyDao taratasyDao) throws URISyntaxException {
    super(taratasyDao);
  }

  @Override
  protected APIGatewayProxyResponseEvent handleSecuredRequest(APIGatewayProxyRequestEvent input, Context context) {
    User ownerUser = super.whoisOwner(input);
    var taratasyList = taratasyDao.findBy(ownerUser.id());
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withBody(taratasyMapper.toRestString(taratasyList));
  }

  @Override
  protected Operation getOperation() {
    return READ;
  }

  @Override
  public void beforeCheckpoint(org.crac.Context<? extends Resource> context) throws Exception {
    APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
        .withPathParameters(Map.of("userId", "dummy"))
        .withHeaders(Map.of(AUTHORIZATION_HEADER, "dummy"));
    apply(requestEvent, null);
  }

  @Override
  public void afterRestore(org.crac.Context<? extends Resource> context) throws Exception {

  }
}
