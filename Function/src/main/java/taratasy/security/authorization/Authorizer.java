package taratasy.security.authorization;

import taratasy.security.authentication.User;
import taratasy.utils.CSVReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Authorizer {
  private final Map<RequestOwner, List<Operation>> authorizations;

  private final int REQUESTER_INDEX_IN_CSV = 0;
  private final int OWNER_INDEX_IN_CSV = 1;
  private final int OPERATION_INDEX_IN_CSV = 2;

  private static final Pattern PREFIXED_ROLE_PATTERN = Pattern.compile("role=(.*)");

  public Authorizer(File authorizationsFile) {
    authorizations = new HashMap<>();

    var authorizationLines = new CSVReader().apply(authorizationsFile);
    authorizationLines.forEach(authorizationLine -> {
      var requester = constructRequester(authorizationLine.get(REQUESTER_INDEX_IN_CSV).trim().toLowerCase());
      var owner = constructOwner(authorizationLine.get(OWNER_INDEX_IN_CSV).trim().toLowerCase());
      var requestOwner = new RequestOwner(requester, owner);
      var operation = Operation.valueOf(
          authorizationLine
              .get(OPERATION_INDEX_IN_CSV)
              .trim()
              .toUpperCase());

      authorizations.computeIfAbsent(requestOwner, k -> new ArrayList<>());
      authorizations.get(requestOwner).add(operation);
    });
  }

  private Requester constructRequester(String prefixedRoleOrUserId) {
    var matcher = PREFIXED_ROLE_PATTERN.matcher(prefixedRoleOrUserId);
    return new Requester(matcher.find() ?
        new User.Role(matcher.group(1)) :
        new User.Id(prefixedRoleOrUserId));
  }

  private Owner constructOwner(String prefixedRoleOrUserId) {
    var matcher = PREFIXED_ROLE_PATTERN.matcher(prefixedRoleOrUserId);
    return new Owner(matcher.find() ?
        new User.Role(matcher.group(1)) :
        new User.Id(prefixedRoleOrUserId));
  }

  public boolean isAuthorized(User requesterUser, User ownerUser, Operation operation) {
    var authorizedOperations = new ArrayList<Operation>();

    addAuthorisations(authorizedOperations, new Requester(requesterUser.id()), new Owner(ownerUser.id()));
    addAuthorisations(authorizedOperations, new Requester(requesterUser.id()), new Owner(ownerUser.role()));
    addAuthorisations(authorizedOperations, new Requester(requesterUser.role()), new Owner(ownerUser.id()));
    addAuthorisations(authorizedOperations, new Requester(requesterUser.role()), new Owner(ownerUser.role()));

    return authorizedOperations.contains(operation);
  }

  private void addAuthorisations(ArrayList<Operation> authorisations, Requester requester, Owner owner) {
    var idToIdAuthorizations = authorizations.get(new RequestOwner(requester, owner));
    if (idToIdAuthorizations != null) {
      authorisations.addAll(idToIdAuthorizations);
    }
  }
}