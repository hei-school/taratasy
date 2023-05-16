package taratasy.security.authorization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import taratasy.security.authentication.User;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static taratasy.security.authorization.Operation.CRUPDATE;
import static taratasy.security.authorization.Operation.DREACTIVATE;
import static taratasy.security.authorization.Operation.READ;

class AuthorizerHeiTest {
  private static Authorizer authorizer;

  private final User.Role studentRole = new User.Role("student");
  private final User.Role teacherRole = new User.Role("teacher");
  private final User.Role managerRole = new User.Role("manager");

  private final User litaStudent = new User(new User.Id("lita"), studentRole);
  private final User bozyTeacher = new User(new User.Id("bozy"), teacherRole);
  private final User bemaTeacher = new User(new User.Id("bema"), teacherRole);
  private final User louManager = new User(new User.Id("lou"), managerRole);

  @BeforeAll
  public static void setUp() throws URISyntaxException {
    var authorizationsResource = AuthorizerHeiTest.class
        .getClassLoader()
        .getResource("authorizations-hei.csv");
    authorizer = new Authorizer(new File(authorizationsResource.toURI()));
  }

  @Test
  public void teachers_can_readonly_students() {
    assertTrue(authorizer.isAuthorized(bozyTeacher, litaStudent, READ));
    assertFalse(authorizer.isAuthorized(bozyTeacher, litaStudent, CRUPDATE));
    assertFalse(authorizer.isAuthorized(bozyTeacher, litaStudent, DREACTIVATE));
  }

  @Test
  public void teachers_can_nothing_on_teachers() {
    assertFalse(authorizer.isAuthorized(bozyTeacher, bemaTeacher, READ));
    assertFalse(authorizer.isAuthorized(bozyTeacher, bemaTeacher, CRUPDATE));
    assertFalse(authorizer.isAuthorized(bozyTeacher, bemaTeacher, DREACTIVATE));
  }

  @Test
  public void teachers_can_nothing_on_managers() {
    assertFalse(authorizer.isAuthorized(bozyTeacher, louManager, READ));
    assertFalse(authorizer.isAuthorized(bozyTeacher, louManager, CRUPDATE));
    assertFalse(authorizer.isAuthorized(bozyTeacher, louManager, DREACTIVATE));
  }
}