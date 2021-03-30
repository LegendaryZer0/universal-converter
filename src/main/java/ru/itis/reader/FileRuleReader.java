package ru.itis.reader;

import au.com.bytecode.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import ru.itis.model.Pair;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
public class FileRuleReader {

  private final HashMap<String, ArrayList<Pair<String, BigDecimal>>> vault;

  public FileRuleReader() {
    vault = new HashMap<>();
  }

  public HashMap<String, ArrayList<Pair<String, BigDecimal>>> read(File fileName) {

    try (CSVReader csvReader = new CSVReader(new java.io.FileReader(fileName, StandardCharsets.UTF_8))) {

      List<String[]> csvConvertConfig = csvReader.readAll();
      for (String[] info : csvConvertConfig) {
        if (info.length < 3) throw new IllegalArgumentException("Incorrect CSV file");

        if (vault.containsKey(info[0])) {
          List<Pair<String, BigDecimal>> temp = vault.get(info[0]);
          temp.add(new Pair<>(info[1], BigDecimal.valueOf(Double.parseDouble(info[2]))));
        } else {
          ArrayList<Pair<String, BigDecimal>> pairs = new ArrayList<>();
          pairs.add(new Pair<>(info[1], BigDecimal.valueOf(Double.parseDouble(info[2]))));
          vault.put(info[0], pairs);
        }

        if (vault.containsKey(info[1])) {
          List<Pair<String, BigDecimal>> temp = vault.get(info[1]);
          temp.add(
              new Pair<>(
                  info[0], new BigDecimal(1).divide(new BigDecimal(info[2]), 25, RoundingMode.UP)));
        } else {
          ArrayList<Pair<String, BigDecimal>> pairs = new ArrayList<>();
          pairs.add(
              new Pair<>(
                  info[0], new BigDecimal(1).divide(new BigDecimal(info[2]), 25, RoundingMode.UP)));
          vault.put(info[1], pairs);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return vault;
  }
}
