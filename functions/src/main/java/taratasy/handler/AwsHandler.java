package taratasy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.function.BiFunction;

public interface AwsHandler
    extends BiFunction<APIGatewayProxyRequestEvent, Context, APIGatewayProxyResponseEvent> {

}
