package com.noumea.digital.assessment;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.noumea.digital.assessment.util.Converter.*;
import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterTest {

  private static final String CSV_CONTENT =
      """
                    Name,Address,Postcode,Phone,Credit Limit,Birthday
                    "Johnson, John",Voorstraat 32,3122gg,020 3849381,10000,01/01/1987
                    "Anderson, Paul",Dorpsplein 3A,4532 AA,030 3458986,109093,03/12/1965
                    "Wicket, Steve",Mendelssohnstraat 54d,3423 ba,0313-398475,934,03/06/1964
                    "Benetar, Pat",Driehoog 3zwart,2340 CC,06-28938945,54,04/09/1964
                    "Gibson, Mal",Vredenburg 21,3209 DD,06-48958986,54.5,09/11/1978
                    "Friendly, User",Sint Jansstraat 32,4220 EE,0885-291029,63.6,10/08/1980
                    "Smith, John",Børkestraße 32,87823,+44 728 889838,9898.3,20/09/1999""";

  private static final String PRN_CONTENT =
      """
                    Name            Address               Postcode Phone         Credit Limit Birthday
                    Johnson, John   Voorstraat 32         3122gg   020 3849381        1000000 19870101
                    Anderson, Paul  Dorpsplein 3A         4532 AA  030 3458986       10909300 19651203
                    Wicket, Steve   Mendelssohnstraat 54d 3423 ba  0313-398475          93400 19640603
                    Benetar, Pat    Driehoog 3zwart       2340 CC  06-28938945           5400 19640904
                    Gibson, Mal     Vredenburg 21         3209 DD  06-48958986           5450 19781109
                    Friendly, User  Sint Jansstraat 32    4220 EE  0885-291029           6360 19800810
                    Smith, John     Børkestraße 32        87823    +44 728 889838      989830 19990920""";

  private static final int[] CHUNK_SIZES = {16, 22, 9, 14, 13, 8};
  private static final List<Integer> DECIMAL_COL_INDEXES = Collections.singletonList(4);
  private static final List<Integer> DATE_COL_INDEXES = Collections.singletonList(5);

  @Test
  void testCsvToJson() throws IOException {
    String fileContent = getTestFileContent("csv.json.txt");
    String workbook = convertCsvFileToJson(CSV_CONTENT);
    assertEquals(workbook, fileContent);
  }

  @Test
  void testPrnToJson() throws IOException {
    String fileContent = getTestFileContent("prnf.json.txt");
    String workbook =
        convertPrnFileToJson(PRN_CONTENT, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES);
    assertEquals(workbook, fileContent);
  }

  @Test
  void testCsvToHtml() throws IOException {
    String fileContent = getTestFileContent("csv.html.txt");
    String workbook = convertCsvFileToHtml(CSV_CONTENT);
    assertEquals(workbook, fileContent);
  }

  @Test
  void testPrnToHtml() throws IOException {
    String fileContent = getTestFileContent("prnf.html.txt");
    String workbook =
        convertPrnFileToHtml(PRN_CONTENT, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES);
    assertEquals(workbook, fileContent);
  }

  @Test
  void compareJson() {
    String workbook1 = convertCsvFileToJson(CSV_CONTENT);
    String workbook2 =
        convertPrnFileToJson(PRN_CONTENT, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES);
    assertEquals(workbook1, workbook2);
  }

  @Test
  void compareHtml() {
    String workbook1 = convertCsvFileToHtml(CSV_CONTENT);
    String workbook2 =
        convertPrnFileToHtml(PRN_CONTENT, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES);
    assertEquals(workbook1, workbook2);
  }

  private static String getTestFileContent(String fileName) throws IOException {
    return new String(readAllBytes(getResource(fileName)));
  }

  private static Path getResource(String fileName) {
    return Paths.get("src", "test", "resources", fileName);
  }
}
