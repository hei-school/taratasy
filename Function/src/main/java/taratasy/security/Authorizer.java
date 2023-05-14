package taratasy.security;

import taratasy.utils.CSVReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Authorizer {
  private final Map<TargetedByPrincipal, List<Operation>> authorizations;

  private final int PRINCIPAL_INDEX_IN_CSV = 0;
  private final int TARGET_INDEX_IN_CSV = 1;
  private final int OPERATION_INDEX_IN_CSV = 2;

  public Authorizer(File authorizationsFile) {
    authorizations = new HashMap<>();

    var authorizationLines = new CSVReader().apply(authorizationsFile);
    authorizationLines.forEach(authorizationLine -> {
      var principal = new Principal(authorizationLine.get(PRINCIPAL_INDEX_IN_CSV));
      var target = new Target(authorizationLine.get(TARGET_INDEX_IN_CSV));
      var targetedByPrincipal = new TargetedByPrincipal(principal, target);
      var operation = Operation.valueOf(authorizationLine.get(OPERATION_INDEX_IN_CSV).toUpperCase());

      authorizations.computeIfAbsent(targetedByPrincipal, k -> new ArrayList<>());
      authorizations.get(targetedByPrincipal).add(operation);
    });
  }

  public boolean isAuthorized(Principal principal, Target target, Operation operation) {
    var authorizedOperations = authorizations.get(new TargetedByPrincipal(principal, target));
    return authorizedOperations != null && authorizedOperations.contains(operation);
  }
}