package com.noumea.digital.assessment;

import static com.noumea.digital.assessment.util.Converter.*;

public class Main {

    private static final String FILE_NAME = "Workbook2";
    private static final int[] CHUNK_SIZES = {16, 22, 9, 14, 13, 8};
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Number of arguments must be 2!");
            return;
        }

        if ("csv".equals(args[0]) && "json".equals(args[1])) {
            System.out.println(convertCsvFileToJson(FILE_NAME));
        } else if ("prn".equals(args[0]) && "json".equals(args[1])) {
            System.out.println(convertPrnFileToJson(FILE_NAME, CHUNK_SIZES));
        } else if ("csv".equals(args[0]) && "html".equals(args[1])) {
            System.out.println(convertCsvFileToHtml(FILE_NAME));
        } else if ("prn".equals(args[0]) && "html".equals(args[1])) {
            System.out.println(convertPrnFileToHtml(FILE_NAME, CHUNK_SIZES));
        } else {
            System.out.println("Please provide the following input format:\nFirst: csv or prn, second: json or html!");
        }
    }
}
