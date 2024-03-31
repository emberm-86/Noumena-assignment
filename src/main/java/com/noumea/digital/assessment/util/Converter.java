package com.noumea.digital.assessment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;
import java.util.stream.IntStream;

public class Converter {
    private static final SimpleDateFormat DATE_FORMATTER_1 = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_FORMATTER_2 = new SimpleDateFormat("yyyyMMdd");
    public static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.DOWN);

    public static String convertPrnFileToJson(String fileName, int... chunkSizes) {
        return convertCsvToJson(convertPrnToCsv(fileName, chunkSizes));
    }

    public static String convertPrnFileToHtml(String fileName, int... chunkSizes) {
        return convertCsvToHtml(convertPrnToCsv(fileName, chunkSizes));
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

    private static byte[] convertPrnToCsv(String fileName, int... chunkSizes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             OutputStreamWriter out = new OutputStreamWriter(bos);
             BufferedWriter writer = new BufferedWriter(out);
             InputStream fis = Converter.class.getResourceAsStream("/" + fileName + ".prn")) {

            Scanner sc = new Scanner(Objects.requireNonNull(fis));
            ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                    .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                    .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                    .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    .build();

            while (sc.hasNextLine()) {
                csvWriter.writeNext(splitStringToChunks(sc.nextLine(), chunkSizes));
            }
            out.flush();
            csvWriter.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String convertCsvFileToJson(String fileName) {
        try {
            return convertCsvToJson(getCsvFileReader(fileName), new CSVParserBuilder()
                    .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                    .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                    .build(), false);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertCsvToJson(byte[] bytes) {
        return convertCsvToJson(new InputStreamReader(new ByteArrayInputStream(bytes)), new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .build(), true);
    }

    private static String convertCsvToJson(Reader reader, CSVParser csvParser, boolean prn) {
        try (CSVReader csvReader = createCsvReader(reader, csvParser)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(getStructure(csvReader.readAll(), prn));
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String convertCsvFileToHtml(String fileName) {
        try {
            return convertCsvToHtml(getCsvFileReader(fileName), new CSVParserBuilder()
                    .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                    .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                    .build(), false);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileReader getCsvFileReader(String fileName) throws FileNotFoundException {
        return new FileReader(Objects.requireNonNull(
                Converter.class.getResource("/" + fileName + ".csv")).getFile());
    }

    private static String convertCsvToHtml(byte[] bytes) {
        return convertCsvToHtml(new InputStreamReader(new ByteArrayInputStream(bytes)), new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .build(), true);
    }

    private static String convertCsvToHtml(Reader reader, CSVParser csvParser, boolean prn) {
        try (CSVReader csvReader = createCsvReader(reader, csvParser);
             StringWriter writer = new StringWriter()) {
            writer.write("<!DOCTYPE html><head><title>Converter Test</title></head><body><table>\n");

            boolean headerRead = false;

            for (String[] strings : csvReader) {
                writer.write("<tr>");
                if (headerRead) {
                    IntStream.range(0, strings.length).forEach(j -> {
                                String cellValue = strings[j];
                                if (prn && j == strings.length - 1) {
                                    try {
                                        cellValue = DATE_FORMATTER_1.format(DATE_FORMATTER_2.parse(strings[j]));
                                    } catch (ParseException e) {
                                        //
                                    }
                                } else if (prn && j == strings.length - 2) {
                                    cellValue = getDecimalCellValue(strings[j]);
                                }
                                writer.write("<td>" + cellValue + "</td>");
                            }
                    );
                } else {
                    Arrays.stream(strings).forEach(cell -> writer.write("<td>" + cell + "</td>"));
                }
                writer.write("</tr>\n");
                headerRead = true;
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

    private static List<Map<String, String>> getStructure(List<String[]> values, boolean prn) {
        String[] header = values.getFirst();
        List<Map<String, String>> structure = new ArrayList<>();

        for (int i = 1; i < values.size(); i++) {
            String[] strings = values.get(i);
            Map<String, String> map = new LinkedHashMap<>();
            for (int j = 0; j < strings.length; j++) {
                String cellValue = strings[j];
                if (prn && "Birthday".equals(header[j])) {
                    try {
                        cellValue = DATE_FORMATTER_1.format(DATE_FORMATTER_2.parse(cellValue));
                    } catch (ParseException e) {
                        //
                    }
                }

                if (prn && "Credit Limit".equals(header[j])) {
                    cellValue = getDecimalCellValue(cellValue);
                }
                map.put(header[j], cellValue);
            }
            structure.add(map);
        }
        return structure;
    }

    private static String getDecimalCellValue(String cellValue) {
        return new BigDecimal(cellValue).divide(new BigDecimal("100.00000"), MATH_CONTEXT)
                .setScale(2, RoundingMode.HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }
}
