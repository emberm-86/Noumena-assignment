package com.noumea.digital.assessment.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormattingProvider {

    private static final SimpleDateFormat DATE_FORMATTER_1 = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_FORMATTER_2 = new SimpleDateFormat("yyyyMMdd");
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;
    private static final MathContext MATH_CONTEXT = new MathContext(9, ROUNDING_MODE);

    public static String getDecimalCellValue(String cellValue) {
        return new BigDecimal(cellValue).divide(new BigDecimal("100"), MATH_CONTEXT)
                .setScale(2, ROUNDING_MODE)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static String getFormattedDate(String str) {
        try {
            return DATE_FORMATTER_1.format(DATE_FORMATTER_2.parse(str));
        } catch (ParseException e) {
            return "#PARSE_ERROR";
        }
    }
}
