package com.noumea.digital.assessment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;

public class Converter {
    private static final SimpleDateFormat DATE_FORMATTER_1 = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_FORMATTER_2 = new SimpleDateFormat("yyyyMMdd");
    public static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.DOWN);

    public static String convertPrnFileToJson(String fileContent, int... chunkSizes) {
        return convertCsvToJson(convertPrnToCsv(fileContent, chunkSizes), new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build());
    }

    public static String convertPrnFileToHtml(String fileContent, int... chunkSizes) {
        return convertCsvToHtml(convertPrnToCsv(fileContent, chunkSizes), new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build());
    }

    private static String[] splitStringToChunks(String inputString, int... chunkSizes) {
        List<String> list = new ArrayList<>();
        int chunkStart, chunkEnd = 0;

        for (int length : chunkSizes) {
            chunkStart = chunkEnd;
            chunkEnd = chunkStart + length;
            String dataChunk = inputString.substring(chunkStart, chunkEnd);
            String trim = dataChunk.trim();
            list.add(trim);
        }
        return list.toArray(new String[0]);
    }

    private static byte[] convertPrnToCsv(String fileContent, int... chunkSizes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             OutputStreamWriter out = new OutputStreamWriter(bos);
             BufferedWriter writer = new BufferedWriter(out);
             InputStream bis = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))) {
            Scanner sc = new Scanner(bis);
            ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                    .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                    .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                    .build();

            boolean headerProcessed = false;

            while (sc.hasNextLine()) {
                String[] strings = splitStringToChunks(sc.nextLine().trim(), chunkSizes);

                if (headerProcessed) {
                    // making prn consistent with the other csv
                    try {
                        strings[strings.length - 1] = DATE_FORMATTER_1.format(
                                DATE_FORMATTER_2.parse(strings[strings.length - 1]));
                    } catch (ParseException e) {
                        //
                    }
                    strings[strings.length - 2] = getDecimalCellValue(strings[strings.length - 2]);
                }
                csvWriter.writeNext(strings);
                headerProcessed = true;
            }
            out.flush();
            csvWriter.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String convertCsvFileToJson(String fileContent) {
        return convertCsvToJson(fileContent.getBytes(StandardCharsets.UTF_8), new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build());
    }

    private static String convertCsvToJson(byte[] bytes, CSVParser csvParser) {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));

        try (CSVReader csvReader = createCsvReader(reader, csvParser)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(getStructure(csvReader.readAll()));
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String convertCsvFileToHtml(String fileContent) {
        return convertCsvToHtml(fileContent.getBytes(StandardCharsets.UTF_8), new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build());
    }

    private static String convertCsvToHtml(byte[] bytes, CSVParser csvParser) {
        return convertCsvToHtml(new InputStreamReader(new ByteArrayInputStream(bytes)), csvParser);
    }

    private static String convertCsvToHtml(Reader reader, CSVParser csvParser) {
        try (CSVReader csvReader = createCsvReader(reader, csvParser);
             StringWriter writer = new StringWriter()) {
            writer.write("<!DOCTYPE html><head><title>Converter Test</title></head><body><table>\n");

            for (String[] strings : csvReader) {
                writer.write("<tr>");
                Arrays.stream(strings).forEach(cell -> writer.write("<td>" + cell.trim() + "</td>"));
                writer.write("</tr>\n");
            }

            writer.write("</table></body></html>\n");
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static CSVReader createCsvReader(Reader reader, CSVParser csvParser) {
        return new CSVReaderBuilder(reader)
                .withCSVParser(csvParser).build();
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

    private static String getDecimalCellValue(String cellValue) {
        return new BigDecimal(cellValue).divide(new BigDecimal("100"), MATH_CONTEXT)
                .setScale(2, RoundingMode.HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }
}
