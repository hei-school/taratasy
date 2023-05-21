package taratasy.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static java.net.http.HttpClient.newHttpClient;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static taratasy.handler.SecuredRequestHandler.AUTHORIZATION_HEADER;

public class GetFilesIT {
  @Test
  public void concurrently_get_files() {
    var callerNb = 100;
    var executor = newFixedThreadPool(100);

    var latch = new CountDownLatch(1);
    var futures = new ArrayList<Future<String>>();
    for (var callerIdx = 0; callerIdx < callerNb; callerIdx++) {
      futures.add(
          executor.submit(() -> getTaratasyList(
              new URI("https://ijhtk0krr9.execute-api.eu-central-1.amazonaws.com/users/litaId/files"),
              "bearer lita",
              latch)));
    }
    latch.countDown();

    List<String> retrieved = futures.stream()
        .map(this::getOptionalFutureResult)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .peek(taratasyList -> assertEquals("[]", taratasyList))
        .toList();
    assertEquals(retrieved.size(), callerNb);
  }

  public String getTaratasyList(URI uri, String bearer, CountDownLatch latch) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .GET()
        .header(AUTHORIZATION_HEADER, bearer)
        .build();

    try {
      latch.await();
      HttpResponse<String> response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @SneakyThrows
  public <T> Optional<T> getOptionalFutureResult(Future<T> future) {
    try {
      return Optional.of(future.get());
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
