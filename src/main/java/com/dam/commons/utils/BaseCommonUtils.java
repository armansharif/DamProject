package com.dam.commons.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.dao.CannotAcquireLockException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dam.commons.utils.BaseDateUtils.getDateDiff;

public class BaseCommonUtils {
    private static String versionRevision;

    public static String changeTerm(String term) {

        char[][] replace = new char[][]{
                {'\'', ' '},
                {'"', ' '},
                {' ', '%'},
//          {(char)55977, (char)55683},//kaf
//          {(char)56204, (char)55690},//ye
                {(char) 1705, (char) 1603},//kaf
                {(char) 1740, (char) 1610},//ye
                {'٠', '0'},
                {'١', '1'},
                {'٢', '2'},
                {'٣', '3'},
                {'٤', '4'},
                {'٥', '5'},
                {'٦', '6'},
                {'٧', '7'},
                {'٨', '8'},
                {'٩', '9'},
        };
        for (int i = 0; i < replace.length; i++) {
            char[] chars = replace[i];
            term = term.replace(chars[0], chars[1]);
        }
        return term;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static final String normalizeString(String s) {
        return s.replace('ی', 'ي').replace('ک', 'ك');
    }

    public static final boolean isNull(String str) {
        return str == null || "".equals(str) || "null".equals(str) || str.trim().isEmpty();
    }

    public static final boolean isNull(Object obj) {
        if (obj == null)
            return true;
        return isNull(obj.toString());
    }

    public static final boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * mansour: Checks if two strings are the same
     */
    public static final boolean isEqual(String toCompareStr, String constStr) {
        return (!isNull(toCompareStr) && !isNull(constStr) &&
                normalizeString(constStr).equals(normalizeString(toCompareStr)));
    }

    public static final boolean isEqual(Object obj, String constStr) {
        if (obj == null)
            return false;
        String str = obj.toString();
        return (isEqual(str, constStr));
    }

    public static final boolean isEqual(Object obj1, Object obj2) {
        return (isNull(obj1) && isNull(obj2)) || (obj1 != null && obj1.equals(obj2));
    }

    public static final String getString(Object object) {
        if (object != null) {
            return object.toString();
        } else {
            return "";
        }
    }

    public static final String getStringValue(Object columnValue) {
        if (isNull(columnValue)) {
            return "";
        } else {

            return columnValue.toString();
        }
    }

    public static Boolean getBooleanValue(Object object){
        if(BaseCommonUtils.isNotNull(object)){
            if(object instanceof Number){
                Integer intResult = NumberUtils.integerValue(object);
                return Objects.equals(intResult, 1);
            }
            else if(object instanceof String){
                String stringResult = getStringValue(object);
                return Boolean.valueOf(stringResult);
            }
        }

        return false;
    }

    public static String reverseDate(String date) {
        try {
            StringTokenizer st = new StringTokenizer(date, "/");
            String part1 = st.nextToken();
            String part2 = st.nextToken();
            String part3 = st.nextToken();
            return part3 + "/" + part2 + "/" + part1;
        } catch (Exception e) {
            return date;
        }
    }

    public static final String reverseCodes(String code) {
        String[] parts = new String[50];
        int partsIndex = 0;
        StringBuffer newPart = new StringBuffer();
        for (int i = 0; i < code.length(); i++) {
            String newChar = code.substring(i, i + 1);
            if (!"-".equals(newChar) && !"/".equals(newChar) && !"_".equals(newChar) &&
                    !".".equals(newChar)) {
                if (i == code.length() - 1) {
                    newPart.append(newChar);
                    parts[partsIndex] = newPart.toString();
                    partsIndex++;
                } else {
                    newPart.append(newChar);
                }
            } else {
                parts[partsIndex] = newPart.toString();
                partsIndex++;
                newPart = new StringBuffer();
                parts[partsIndex] = newChar;
                partsIndex++;
            }
        }
        StringBuffer reverseCode = new StringBuffer();
        for (int i = partsIndex; i > 0; i--) {
            reverseCode.append(parts[i - 1]);
        }
        return reverseCode.toString();
    }

    public static final String reverseAccountNumber(String accountNumber) {
        try {
            StringTokenizer st = new StringTokenizer(accountNumber, "-");
            ArrayList parts = new ArrayList();
            while (st.hasMoreTokens()) {
                parts.add(st.nextToken());
            }
            String reversedAccountNumber = "";
            for (int i = parts.size() - 1; i >= 0; i--) {
                reversedAccountNumber += parts.get(i);
                if (i > 0) {
                    reversedAccountNumber += "-";
                }
            }
            return reversedAccountNumber;
        } catch (Exception e) {
            return accountNumber;
        }
    }

    public static final List<Thread> getThreadsFor(Runnable myRunnable) throws Exception {
        Method getThreads = Thread.class.getDeclaredMethod("getThreads");
        Field target = Thread.class.getDeclaredField("target");
        target.setAccessible(true);
        getThreads.setAccessible(true);
        Thread[] threads = (Thread[]) getThreads.invoke(null);
        List<Thread> result = new ArrayList<Thread>();
        for (Thread thread : threads) {
            Object runnable = target.get(thread);
            if (runnable == myRunnable)
                result.add(thread);
        }
        return result;
    }



    public static final String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }



    public static String getCommaSeparatedForQuery(List<Map<String, Object>> list, String fieldName) {
        if (list != null && !list.isEmpty()) {
            StringBuffer numbers = new StringBuffer();
            for (Map<String, Object> number : list) {
                numbers.append(number.get(fieldName)).append(" , ");
            }
            numbers.delete(numbers.length() - 3, numbers.length());
            return numbers.toString();
        }
        return null;
    }

    public static String fixFarsiNumbers(String s) {
        s = s.replace((char) (1776), (char) (48));              // 0
        s = s.replace((char) (1777), (char) (49));              // 1
        s = s.replace((char) (1778), (char) (50));              // 2
        s = s.replace((char) (1779), (char) (51));              // 3
        s = s.replace((char) (1780), (char) (52));              // 4
        s = s.replace((char) (1781), (char) (53));              // 5
        s = s.replace((char) (1782), (char) (54));              // 6
        s = s.replace((char) (1783), (char) (55));              // 7
        s = s.replace((char) (1784), (char) (56));              // 8
        s = s.replace((char) (1785), (char) (57));              // 9
        return s;
    }


    public static Map<String, List> fillBatchExecute(Map<String, List> batchQuery, String sql, Object... params) {
        batchQuery.computeIfAbsent(sql, s -> new ArrayList()).add(params);
        return batchQuery;
    }


    public static String getCommaSeparatedValue(Collection<?> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static boolean isShebaNumberValid(String shebaNo) {
        return isNotNull(shebaNo) && Pattern.matches("^(?:IR)(?=.{24}$)[0-9]*$", shebaNo) && checkSheba(shebaNo);
    }

    public static boolean isNationalCodeValid(String nationalCode) {
        boolean isValid = false;
        if (nationalCode != null && nationalCode.length() == 10) {
            if (StringUtils.isNumeric(nationalCode)) {
                AtomicInteger index = new AtomicInteger();
                int controllerConditionNumber = nationalCode.substring(0, 9).chars()
                        .map(i -> Integer.valueOf(Character.toString((char) i)) * (10 - index.getAndIncrement())).sum() % 11;
                int controllerNumber = Integer.valueOf(nationalCode.substring(9));
                isValid = (controllerConditionNumber < 2) ?
                        controllerConditionNumber == controllerNumber :
                        controllerNumber == (11 - controllerConditionNumber);
            }
        }
        return isValid;
    }
    public static boolean isAccountNumberValid(String shebaNo) {
        return isNotNull(shebaNo) && Pattern.matches("^[0-9/.-]+$", shebaNo);
    }

    private static boolean checkSheba(String shebaNo) {
        try {
            String temp = shebaNo.toUpperCase().replace("IR", "").concat("1827");
            temp = temp.substring(2) + temp.substring(0, 2);
            return NumberUtils.bigDecimalValue(temp).divideAndRemainder(BigDecimal.valueOf(97))[1].equals(BigDecimal.ONE);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getVersionRevision() {
        // suppose that there is a file with this name in classpath generated by jenkins.
        if (versionRevision == null) {
            try {
                versionRevision = "." + IOUtils.toString(BaseCommonUtils.class.getClassLoader().getResourceAsStream("revision"),
                        StandardCharsets.UTF_8).split("\n")[0];
            } catch (Exception e) {
                versionRevision = "";
            }
        }
        return versionRevision;
    }

    public static List<String[]> getSplitArray(String[] strings, int len) {
        List<String[]> objects = new ArrayList<>();
        double to = Math.ceil((double) strings.length / len);
        for (int i = 0; i < to; i++) {
            int fromIndex = i * len;
            int toIndex = (i * len) + len;
            if (toIndex > strings.length)
                toIndex = strings.length;
            objects.add(Arrays.asList(strings).subList(fromIndex, toIndex).toArray(new String[0]));
        }
        return objects;
    }


}
