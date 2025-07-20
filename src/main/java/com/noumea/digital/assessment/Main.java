package com.noumea.digital.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static com.noumea.digital.assessment.util.Converter.*;

public class Main {

  private static final int[] CHUNK_SIZES = {16, 22, 9, 14, 13, 8};
  private static final String CSV = "csv";
  private static final String PRN = "prn";
  private static final String JSON = "json";
  private static final String HTML = "html";

  private static final List<Integer> DECIMAL_COL_INDEXES = Collections.singletonList(4);
  private static final List<Integer> DATE_COL_INDEXES = Collections.singletonList(5);
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    if (args.length != 3) {
      LOGGER.info("Number of arguments must be 3!");
      return;
    }

    String inputType = args[0];
    String outputType = args[1];
    String fileContent = new String(Base64.getDecoder().decode(args[2]), StandardCharsets.UTF_8);

    if (!LOGGER.isInfoEnabled()) {
      LOGGER.error("Please check your log config info is not enabled!");
      return;
    }

    if (CSV.equals(inputType) && JSON.equals(outputType)) {
      LOGGER.info(convertCsvFileToJson(fileContent));
    } else if (PRN.equals(inputType) && JSON.equals(outputType)) {
      LOGGER.info(
          convertPrnFileToJson(fileContent, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES));
    } else if (CSV.equals(inputType) && HTML.equals(outputType)) {
      LOGGER.info(convertCsvFileToHtml(fileContent));
    } else if (PRN.equals(inputType) && HTML.equals(outputType)) {
      LOGGER.info(
          convertPrnFileToHtml(fileContent, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES));
    } else {
      LOGGER.info(
          "Please provide the following input format:\n"
              + "First: csv or prn, second: json or html!");
    }
  }
}
