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

  private static final Pattern PREFIXED_ROLE_PATTERN = Pattern.compile("@role:(.*)");
  private static final String SELF_KEYWORD = "@self";

  public Authorizer(File authorizationsFile) {
    authorizations = new HashMap<>();

    var authorizationLines = new CSVReader().apply(authorizationsFile);
    authorizationLines.forEach(authorizationLine -> {
      var requester = constructRequester(authorizationLine.get(REQUESTER_INDEX_IN_CSV).trim().toLowerCase());
      var owner = constructOwner(authorizationLine.get(OWNER_INDEX_IN_CSV).trim().toLowerCase());
      var requestOwner = new RequestOwner(requester, owner);
      var operation = Operation.valueOf(authorizationLine.get(OPERATION_INDEX_IN_CSV).trim().toUpperCase());

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
    var selfReplacedAuthorizations = replaceSelfBy(requesterUser);

    var authorizedOperations = new ArrayList<Operation>();
    var idDefRequester = new Requester(requesterUser.id());
    var roleDefRequester = new Requester(requesterUser.role());
    var idDefOwner = new Owner(ownerUser.id());
    var roleDefOwner = new Owner(ownerUser.role());
    computeAuthorizedOperations(selfReplacedAuthorizations, idDefRequester, idDefOwner, authorizedOperations);
    computeAuthorizedOperations(selfReplacedAuthorizations, idDefRequester, roleDefOwner, authorizedOperations);
    computeAuthorizedOperations(selfReplacedAuthorizations, roleDefRequester, idDefOwner, authorizedOperations);
    computeAuthorizedOperations(selfReplacedAuthorizations, roleDefRequester, roleDefOwner, authorizedOperations);

    return authorizedOperations.contains(operation);
  }

  private Map<RequestOwner, List<Operation>> replaceSelfBy(User requesterUser) {
    var res = new HashMap<RequestOwner, List<Operation>>();
    authorizations.forEach((requestOwner, operations) -> {
      var actualRequester = requestOwner.requester();
      var replacedRequester = SELF_KEYWORD.equals(actualRequester.accessId().value()) ?
          new Requester(requesterUser.id()) :
          actualRequester;

      var actualOwner = requestOwner.owner();
      var replacedOwner = SELF_KEYWORD.equals(actualOwner.accessId().value()) ?
          new Owner(requesterUser.id()) :
          actualOwner;

      res.put(new RequestOwner(replacedRequester, replacedOwner), operations);
    });
    return res;
  }

  private void computeAuthorizedOperations(
      Map<RequestOwner, List<Operation>> authorizations,
      Requester requester,
      Owner owner,
      ArrayList<Operation> res) {
    var authorizedOperations = authorizations.get(new RequestOwner(requester, owner));
    if (authorizedOperations != null) {
      res.addAll(authorizedOperations);
    }
  }
}