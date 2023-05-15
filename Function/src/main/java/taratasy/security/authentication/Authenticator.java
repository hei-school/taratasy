package taratasy.security.authentication;

import java.util.function.Function;

public interface Authenticator extends Function<Bearer, Whoami> {
}
