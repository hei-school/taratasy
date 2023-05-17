package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import taratasy.dao.TaratasyDao;
import taratasy.security.authentication.User;
import taratasy.security.authorization.Operation;

import java.net.URISyntaxException;

import static taratasy.security.authorization.Operation.READ;

public class GetFilesHandler extends SecuredRequestHandler {

  private final ObjectMapper om;

  public GetFilesHandler() throws URISyntaxException {
    super();
    om = new ObjectMapper();
  }

  @Override
  protected APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    TaratasyDao taratasyDao = new TaratasyDao();
    User ownerUser = super.whoisOwner(input);
    try {
      return new APIGatewayProxyResponseEvent()
          .withStatusCode(200)
          .withBody(om.writeValueAsString(taratasyDao.findBy(ownerUser.id())));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Operation getOperation() {
    return READ;
  }
}
