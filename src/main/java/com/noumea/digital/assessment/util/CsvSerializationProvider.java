package com.noumea.digital.assessment.util;

import com.opencsv.*;

import java.io.BufferedWriter;
import java.io.Reader;

public class CsvSerializationProvider {

    public static ICSVWriter createDefaultCsvWriter(BufferedWriter writer) {
        return new CSVWriterBuilder(writer)
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build();
    }

    public static CSVReader createDefaultCsvReader(Reader reader) {
        return new CSVReaderBuilder(reader).withCSVParser(createDefaultParser()).build();
    }

    private static CSVParser createDefaultParser() {
        return new CSVParserBuilder()
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build();
    }
}
