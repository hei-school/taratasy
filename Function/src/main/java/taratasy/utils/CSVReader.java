package taratasy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class CSVReader implements Function<File, List<List<String>>> {
  private static final String COMMA_DELIMITER = ",";

  @Override
  public List<List<String>> apply(File file) {
    List<List<String>> records = new ArrayList<>();
    try (Scanner scanner = new Scanner(file)) {
      scanner.nextLine(); // first line is header
      while (scanner.hasNextLine()) {
        records.add(getRecordFromLine(scanner.nextLine()));
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return records;
  }

  private List<String> getRecordFromLine(String line) {
    return Arrays.asList(line.split(COMMA_DELIMITER));
  }
}
