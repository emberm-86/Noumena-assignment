package com.noumea.digital.assessment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.noumea.digital.assessment.util.CsvSerializationProvider.*;
import static com.noumea.digital.assessment.util.FormattingProvider.*;

public class Converter {

  private static final String LINE_ENDING = System.lineSeparator();

  private Converter() {
  }

  public static String convertPrnFileToJson(
      String fileContent,
      List<Integer> decimalColIndexes,
      List<Integer> dateColIndexes,
      int... chunkSizes) {

    return convertCsvToJson(
        convertPrnToCsv(fileContent, decimalColIndexes, dateColIndexes, chunkSizes));
  }

  public static String convertPrnFileToHtml(
      String fileContent,
      List<Integer> decimalColIndexes,
      List<Integer> dateColIndexes,
      int... chunkSizes) {

    return convertCsvToHtml(
        convertPrnToCsv(fileContent, decimalColIndexes, dateColIndexes, chunkSizes));
  }

  private static byte[] convertPrnToCsv(
      String fileContent,
      List<Integer> decimalColIndexes,
      List<Integer> dateColIndexes,
      int... chunkSizes) {

    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(bos);
        BufferedWriter bufferedWriter = new BufferedWriter(out);
        InputStream bis = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))) {

      ICSVWriter csvWriter = createDefaultCsvWriter(bufferedWriter);
      convertPrnToCsv(chunkSizes, bis, csvWriter, decimalColIndexes, dateColIndexes);

      out.flush();
      csvWriter.close();
      return bos.toByteArray();
    } catch (IOException e) {
      throw new BusinessException(e.getMessage(), e);
    }
  }

  private static void convertPrnToCsv(
      int[] chunkSizes,
      InputStream bis,
      ICSVWriter csvWriter,
      List<Integer> decimalColIndexes,
      List<Integer> dateColIndexes) {
    Scanner sc = new Scanner(bis);
    boolean headerProcessed = false;

    while (sc.hasNextLine()) {
      String[] strings = splitStringToChunks(sc.nextLine().trim(), chunkSizes);

      if (headerProcessed) {
        // making prn consistent with the csv
        dateColIndexes.forEach(i -> strings[i] = getFormattedDate(strings[i]));
        decimalColIndexes.forEach(i -> strings[i] = getDecimalCellValue(strings[i]));
      }
      csvWriter.writeNext(strings);
      headerProcessed = true;
    }
  }

  public static String convertCsvFileToJson(String fileContent) {
    return convertCsvToJson(fileContent.getBytes(StandardCharsets.UTF_8));
  }

  public static String convertCsvFileToHtml(String fileContent) {
    return convertCsvToHtml(fileContent.getBytes(StandardCharsets.UTF_8));
  }

  private static String convertCsvToJson(byte[] bytes) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));

    try (CSVReader csvReader = createDefaultCsvReader(reader)) {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      return mapper.writeValueAsString(getStructure(csvReader.readAll()));
    } catch (IOException | CsvException e) {
      throw new BusinessException(e.getMessage(), e);
    }
  }

  private static String convertCsvToHtml(byte[] bytes) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));

    try (CSVReader csvReader = createDefaultCsvReader(reader);
        StringWriter writer = new StringWriter()) {

      writer.write("<!DOCTYPE html><head><title>Converter Test</title></head><body><table>");
      writer.write(LINE_ENDING);

      for (String[] strings : csvReader) {
        writer.write("<tr>");
        Arrays.stream(strings).forEach(cell -> writer.write("<td>" + cell.trim() + "</td>"));
        writer.write("</tr>");
        writer.write(LINE_ENDING);
      }

      writer.write("</table></body></html>");
      writer.write(LINE_ENDING);
      return writer.toString();
    } catch (IOException e) {
      throw new BusinessException(e.getMessage(), e);
    }
  }

  private static String[] splitStringToChunks(String inputString, int... chunkSizes) {
    List<String> list = new ArrayList<>();
    int chunkStart;
    int chunkEnd = 0;

    for (int length : chunkSizes) {
      chunkStart = chunkEnd;
      chunkEnd = chunkStart + length;
      String dataChunk = inputString.substring(chunkStart, chunkEnd);
      String trim = dataChunk.trim();
      list.add(trim);
    }
    return list.toArray(new String[0]);
  }

  private static List<Map<String, String>> getStructure(List<String[]> values) {
    String[] header = values.getFirst();
    List<Map<String, String>> structure = new ArrayList<>();

    for (int i = 1; i < values.size(); i++) {
      String[] strings = values.get(i);
      Map<String, String> map = new LinkedHashMap<>();
      for (int j = 0; j < strings.length; j++) {
        map.put(header[j], strings[j].trim());
      }
      structure.add(map);
    }
    return structure;
  }
}
