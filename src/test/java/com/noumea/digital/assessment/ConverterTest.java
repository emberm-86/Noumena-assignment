package com.noumea.digital.assessment;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.noumea.digital.assessment.util.Converter.*;
import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTest {

    @Test
    public void testCsvToJson() throws IOException {
        String fileContent = new String(readAllBytes(getResource("csv.json.txt")));
        String workbook2 = convertCsvFileToJson("Workbook2");
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void testPrnToJson() throws IOException {
        String fileContent = new String(readAllBytes(getResource("prnf.json.txt")));
        String workbook2 = convertPrnFileToJson("Workbook2", 16, 22, 9, 14, 13, 8);
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void testCsvToHtml() throws IOException {
        String fileContent = new String(readAllBytes(getResource("csv.html.txt")));
        String workbook2 = convertCsvFileToHtml("Workbook2");
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void testPrnToHtml() throws IOException {
        String fileContent = new String(readAllBytes(getResource("prnf.html.txt")));
        String workbook2 = convertPrnFileToHtml("Workbook2", 16, 22, 9, 14, 13, 8);
        assertEquals(workbook2, fileContent);
    }

    @Test
    public void compareJson() {
        String workbook1 = convertCsvFileToJson("Workbook2");
        String workbook2 = convertPrnFileToJson("Workbook2", 16, 22, 9, 14, 13, 8);
        assertEquals(workbook1, workbook2);
    }

    @Test
    public void compareHtml() {
        String workbook1 = convertCsvFileToHtml("Workbook2");
        String workbook2 = convertPrnFileToHtml("Workbook2", 16, 22, 9, 14, 13, 8);
        assertEquals(workbook1, workbook2);
    }

    private static Path getResource(String fileName) {
        return Paths.get("src","test", "resources", fileName);
    }
}
