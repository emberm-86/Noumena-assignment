package com.noumea.digital.assessment;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.noumea.digital.assessment.util.Converter.*;
import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTest {

    String CSV_CONTENT =
            """
            Name,Address,Postcode,Phone,Credit Limit,Birthday
            "Johnson, John",Voorstraat 32,3122gg,020 3849381,10000,01/01/1987
            "Anderson, Paul",Dorpsplein 3A,4532 AA,030 3458986,109093,03/12/1965
            "Wicket, Steve",Mendelssohnstraat 54d,3423 ba,0313-398475,934,03/06/1964
            "Benetar, Pat",Driehoog 3zwart,2340 CC,06-28938945,54,04/09/1964
            "Gibson, Mal",Vredenburg 21,3209 DD,06-48958986,54.5,09/11/1978
            "Friendly, User",Sint Jansstraat 32,4220 EE,0885-291029,63.6,10/08/1980
            "Smith, John",Borkestraße 32,87823,+44 728 889838,9898.3,20/09/1999""";

    String PRN_CONTENT =
            """
            Name            Address               Postcode Phone         Credit Limit Birthday
            Johnson, John   Voorstraat 32         3122gg   020 3849381        1000000 19870101
            Anderson, Paul  Dorpsplein 3A         4532 AA  030 3458986       10909300 19651203
            Wicket, Steve   Mendelssohnstraat 54d 3423 ba  0313-398475          93400 19640603
            Benetar, Pat    Driehoog 3zwart       2340 CC  06-28938945           5400 19640904
            Gibson, Mal     Vredenburg 21         3209 DD  06-48958986           5450 19781109
            Friendly, User  Sint Jansstraat 32    4220 EE  0885-291029           6360 19800810
            Smith, John     Borkestraße 32        87823    +44 728 889838      989830 19990920""";

    @Test
    public void testCsvToJson() throws IOException {
        String fileContent = new String(readAllBytes(getResource("csv.json.txt")));
        String workbook2 = convertCsvFileToJson(CSV_CONTENT);
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void testPrnToJson() throws IOException {
        String fileContent = new String(readAllBytes(getResource("prnf.json.txt")));
        String workbook2 = convertPrnFileToJson(PRN_CONTENT, 16, 22, 9, 14, 13, 8);
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void testCsvToHtml() throws IOException {
        String fileContent = new String(readAllBytes(getResource("csv.html.txt")));
        String workbook2 = convertCsvFileToHtml(CSV_CONTENT);
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void testPrnToHtml() throws IOException {
        String fileContent = new String(readAllBytes(getResource("prnf.html.txt")));
        String workbook2 = convertPrnFileToHtml(PRN_CONTENT, 16, 22, 9, 14, 13, 8);
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void compareJson() {
        String workbook1 = convertCsvFileToJson(CSV_CONTENT);
        String workbook2 = convertPrnFileToJson(PRN_CONTENT, 16, 22, 9, 14, 13, 8);
        assertEquals(workbook1, workbook2);
    }

    @Test
    public void compareHtml() {
        String workbook1 = convertCsvFileToHtml(CSV_CONTENT);
        String workbook2 = convertPrnFileToHtml(PRN_CONTENT, 16, 22, 9, 14, 13, 8);
        assertEquals(workbook1, workbook2);
    }

    private static Path getResource(String fileName) {
        return Paths.get("src","test", "resources", fileName);
    }
}
