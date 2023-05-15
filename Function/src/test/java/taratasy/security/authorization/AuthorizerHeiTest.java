package taratasy.security.authorization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static taratasy.security.authorization.Operation.CRUPDATE;
import static taratasy.security.authorization.Operation.DREACTIVATE;
import static taratasy.security.authorization.Operation.READ;

class AuthorizerHeiTest {
  private static Authorizer authorizer;

  private final Principal principalStudent = new Principal("student");
  private final Principal principalTeacher = new Principal("teacher");
  private final Principal principalManager = new Principal("manager");
  private final Target targetStudent = new Target("student");
  private final Target targetTeacher = new Target("teacher");
  private final Target targetManager = new Target("manager");

  @BeforeAll
  public static void setUp() throws URISyntaxException {
    var authorizationsResource = AuthorizerHeiTest.class
        .getClassLoader()
        .getResource("authorizations-hei.csv");
    authorizer = new Authorizer(new File(authorizationsResource.toURI()));
  }

  @Test
  public void teachers_can_readonly_students() {
    assertTrue(authorizer.isAuthorized(principalTeacher, targetStudent, READ));
    assertFalse(authorizer.isAuthorized(principalTeacher, targetStudent, CRUPDATE));
    assertFalse(authorizer.isAuthorized(principalTeacher, targetStudent, DREACTIVATE));
  }

  @Test
  public void teachers_can_nothing_on_teachers() {
    assertFalse(authorizer.isAuthorized(principalTeacher, targetTeacher, READ));
    assertFalse(authorizer.isAuthorized(principalTeacher, targetTeacher, CRUPDATE));
    assertFalse(authorizer.isAuthorized(principalTeacher, targetTeacher, DREACTIVATE));
  }

  @Test
  public void teachers_can_nothing_on_managers() {
    assertFalse(authorizer.isAuthorized(principalTeacher, targetManager, READ));
    assertFalse(authorizer.isAuthorized(principalTeacher, targetManager, CRUPDATE));
    assertFalse(authorizer.isAuthorized(principalTeacher, targetManager, DREACTIVATE));
  }
}