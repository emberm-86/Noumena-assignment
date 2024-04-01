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

        // System.out.println(fileContent);

     /*   String fileContent =
        "Name            Address               Postcode Phone         Credit Limit Birthday\n" +
        "Johnson, John   Voorstraat 32         3122gg   020 3849381        1000000 19870101\n" +
        "Anderson, Paul  Dorpsplein 3A         4532 AA  030 3458986       10909300 19651203\n" +
        "Wicket, Steve   Mendelssohnstraat 54d 3423 ba  0313-398475          93400 19640603\n" +
        "Benetar, Pat    Driehoog 3zwart       2340 CC  06-28938945           5400 19640904\n" +
        "Gibson, Mal     Vredenburg 21         3209 DD  06-48958986           5450 19781109\n" +
        "Friendly, User  Sint Jansstraat 32    4220 EE  0885-291029           6360 19800810\n" +
        "Smith, John     BorkestraĂźe 32       87823    +44 728 889838      989830 19990920\n";*/
      /*  String fileContent =
        "Name,Address,Postcode,Phone,Credit Limit,Birthday\n" +
        "\"Johnson, John\",Voorstraat 32,3122gg,020 3849381,10000,01/01/1987\n" +
        "\"Anderson, Paul\",Dorpsplein 3A,4532 AA,030 3458986,109093,03/12/1965\n" +
        "\"Wicket, Steve\",Mendelssohnstraat 54d,3423 ba,0313-398475,934,03/06/1964\n" +
        "\"Benetar, Pat\",Driehoog 3zwart,2340 CC,06-28938945,54,04/09/1964\n" +
        "\"Gibson, Mal\",Vredenburg 21,3209 DD,06-48958986,54.5,09/11/1978\n" +
        "\"Friendly, User\",Sint Jansstraat 32,4220 EE,0885-291029,63.6,10/08/1980\n" +
        "\"Smith, John\",Borkestraße 32,87823,+44 728 889838,9898.3,20/09/1999";*/


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
