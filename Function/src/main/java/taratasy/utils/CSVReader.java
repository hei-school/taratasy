package taratasy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class CSVReader implements Function<File, List<List<String>>> {
  private static final String COMMA_DELIMITER = ",";

  @Override
  public List<List<String>> apply(File file) {
    List<List<String>> records = new ArrayList<>();
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        records.add(getRecordFromLine(scanner.nextLine()));
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return records;
  }

  private List<String> getRecordFromLine(String line) {
    var values = new ArrayList<String>();
    try (Scanner rowScanner = new Scanner(line)) {
      rowScanner.useDelimiter(COMMA_DELIMITER);
      while (rowScanner.hasNext()) {
        values.add(rowScanner.next());
      }
    }
    return values;
  }
}
