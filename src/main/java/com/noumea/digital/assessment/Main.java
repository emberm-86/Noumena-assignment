package com.noumea.digital.assessment;

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

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Number of arguments must be 3!");
            return;
        }

        String inputType = args[0];
        String outputType = args[1];
        String fileContent = args[2];

        if (CSV.equals(inputType) && JSON.equals(outputType)) {
            System.out.println(convertCsvFileToJson(fileContent));
        } else if (PRN.equals(inputType) && JSON.equals(outputType)) {
            System.out.println(convertPrnFileToJson(fileContent, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES));
        } else if (CSV.equals(inputType) && HTML.equals(outputType)) {
            System.out.println(convertCsvFileToHtml(fileContent));
        } else if (PRN.equals(inputType) && HTML.equals(outputType)) {
            System.out.println(convertPrnFileToHtml(fileContent, DECIMAL_COL_INDEXES, DATE_COL_INDEXES, CHUNK_SIZES));
        } else {
            System.out.println("Please provide the following input format:\n" +
                    "First: csv or prn, second: json or html!");
        }
    }
}
