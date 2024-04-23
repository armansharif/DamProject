package com.dam.commons.utils;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class NumberUtils {
    static Map<Long, String> baseStrings = new HashMap<>();

    static {
        baseStrings.put(0L, "صفر");
        baseStrings.put(1L, "يک");
        baseStrings.put(2L, "دو");
        baseStrings.put(3L, "سه");
        baseStrings.put(4L, "چهار");
        baseStrings.put(5L, "پنج");
        baseStrings.put(6L, "شش");
        baseStrings.put(7L, "هفت");
        baseStrings.put(8L, "هشت");
        baseStrings.put(9L, "نه");
        baseStrings.put(10L, "ده");
        baseStrings.put(11L, "يازده");
        baseStrings.put(12L, "دوازده");
        baseStrings.put(13L, "سيزده");
        baseStrings.put(14L, "چهارده");
        baseStrings.put(15L, "پانزده");
        baseStrings.put(16L, "شانزده");
        baseStrings.put(17L, "هفده");
        baseStrings.put(18L, "هجده");
        baseStrings.put(19L, "نوزده");
        baseStrings.put(20L, "بيست");
        baseStrings.put(30L, "سي");
        baseStrings.put(40L, "چهل");
        baseStrings.put(50L, "پنجاه");
        baseStrings.put(60L, "شصت");
        baseStrings.put(70L, "هفتاد");
        baseStrings.put(80L, "هشتاد");
        baseStrings.put(90L, "نود");
        baseStrings.put(100L, "یکصد");
        baseStrings.put(200L, "دويست");
        baseStrings.put(300L, "سيصد");
        baseStrings.put(400L, "چهارصد");
        baseStrings.put(500L, "پانصد");
        baseStrings.put(600L, "ششصد");
        baseStrings.put(700L, "هفتصد");
        baseStrings.put(800L, "هشتصد");
        baseStrings.put(900L, "نهصد");
        baseStrings.put(1000L, "هزار");
        baseStrings.put(1000000L, "ميليون");
        baseStrings.put(1000000000L, "ميليارد");
        baseStrings.put(1000000000000L, " بیلیون ");
        baseStrings.put(1000000000000000L, "ميليون ميليارد");
        baseStrings.put(1000000000000000000L, "ميليارد ميليارد");
    }

    public static long getX(long x) {
        long c = 0;
        while ((x = x / 1000) > 0)
            c++;
        c = (long) Math.pow(1000, c);
        return c;
    }

    public static StringBuffer getAsString(long n) {
        if (n >= 1000) {
            String s = baseStrings.get(n);
            if (s != null) {
                StringBuffer sb = new StringBuffer();
                sb.append("یک ");
                sb.append(s);
                return sb;
            }
        }
        return getAsStringInternal(n);
    }

    private static StringBuffer getAsStringInternal(long n) {
        StringBuffer sb = new StringBuffer();
        String s = baseStrings.get(n);
        if (s != null) {
            sb.append(s);
            return sb;
        }
        if (n < 0) {
            sb.append("-");
            return sb.append(getAsStringInternal(-n));
        } else if (n < 1000) {
            long n3 = (n < 100) ? 10 : 100;
            long n1 = n % n3;
            long n2 = n - n1;
            sb.append(getAsStringInternal(n2));
            if (n1 > 0) {
                sb.append(" و ").append(getAsStringInternal(n1));
            }
        } else {
            long n3 = getX(n);
            long n1 = n % n3;
            long n2 = n / n3;
            sb.append(getAsStringInternal(n2)).append(' ').append(getAsStringInternal(n3));
            if (n1 > 0) {
                sb.append(" و ").append(getAsStringInternal(n1));
            }
        }
        return sb;
    }

    public static void main(String[] args) {
        System.out.println(getAsString(100));
        System.out.println(getAsString(100000));
        System.out.println(getAsString(136890));
        System.out.println(getAsString(1000));
        System.out.println(getAsString(1020));
        System.out.println(getAsString(1121));
    }

    public static Long longValue(Object number) {
        return longValue(number, null);
    }

    public static Long longValue(Object number, Long defaultValue) {
        if (BaseCommonUtils.isNull(number))
            return defaultValue;
        else if (number instanceof Number)
            return ((Number) number).longValue();
        else
            try {
                return Long.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }

    public static Long longValueScalar(Object number) {
        return longValue(number, 0L);
    }


    public static Integer integerValue(Object number) {
        return integerValue(number, null);
    }

    public static Integer integerValue(Object number, Integer defaultValue) {
        if (BaseCommonUtils.isNull(number))
            return defaultValue;
        else if (number instanceof Number)
            return ((Number) number).intValue();
        else
            try {
                return Integer.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }

    public static Integer integerValueScalar(Object number) {
        return integerValue(number, 0);
    }

    public static Double doubleValue(Object number) {
        return doubleValue(number, null);
    }

    public static Double doubleValue(Object number, Double defaultValue) {
        if (BaseCommonUtils.isNull(number))
            return defaultValue;
        else if (number instanceof Number)
            return ((Number) number).doubleValue();
        else
            try {
                return Double.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }

    public static Double doubleValueScalar(Object number) {
        return doubleValue(number, 0D);
    }

    public static Float floatValue(Object number) {
        return floatValue(number, null);
    }

    public static Float floatValue(Object number, Float defaultValue) {
        if (BaseCommonUtils.isNull(number))
            return defaultValue;
        else if (number instanceof Number)
            return ((Number) number).floatValue();
        else
            try {
                return Float.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }

    public static Float floatValueScalar(Object number) {
        return floatValue(number, 0F);
    }

    public static BigDecimal bigDecimalValue(Object number, BigDecimal defaultValue) {
        if (BaseCommonUtils.isNull(number))
            return defaultValue;
        else if (number instanceof BigDecimal)
            return ((BigDecimal) number);
        else if (number instanceof Long)
            return BigDecimal.valueOf((Long) number);
        else if (number instanceof Double)
            return BigDecimal.valueOf((Double) number);
        else
            try {
                return new BigDecimal(number.toString().trim());
            } catch (Exception e) {
                return null;
            }
    }

    public static BigDecimal bigDecimalValue(Object number) {
        return bigDecimalValue(number, null);
    }

    public static BigDecimal bigDecimalValueScalar(Object number) {
        return bigDecimalValue(number, BigDecimal.ZERO);
    }

}

