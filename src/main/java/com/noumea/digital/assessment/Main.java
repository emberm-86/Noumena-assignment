package com.noumea.digital.assessment;

import static com.noumea.digital.assessment.util.Converter.*;

public class Main {

    private static final int[] CHUNK_SIZES = {16, 22, 9, 14, 13, 8};
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Number of arguments must be 3!");
            return;
        }

        String inputType = args[0];
        String outputType = args[1];
        String fileContent = args[2];

        if ("csv".equals(inputType) && "json".equals(outputType)) {
            System.out.println(convertCsvFileToJson(fileContent));
        } else if ("prn".equals(inputType) && "json".equals(outputType)) {
            System.out.println(convertPrnFileToJson(fileContent, CHUNK_SIZES));
        } else if ("csv".equals(inputType) && "html".equals(outputType)) {
            System.out.println(convertCsvFileToHtml(fileContent));
        } else if ("prn".equals(inputType) && "html".equals(outputType)) {
            System.out.println(convertPrnFileToHtml(fileContent, CHUNK_SIZES));
        } else {
            System.out.println("Please provide the following input format:\nFirst: csv or prn, second: json or html!");
        }
    }
}
