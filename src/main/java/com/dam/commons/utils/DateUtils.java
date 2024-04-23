package com.dam.commons.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class DateUtils extends BaseDateUtils {


    private static String[] days = {
            "یکشنبه",
            "دوشنبه",
            "سه شنبه",
            "چهارشنبه",
            "پنج شنبه",
            "جمعه",
            "شنبه",
    };

    public static String getPastDate(int dayCnt) {
        long cnt = dayCnt;
        Date date = new Date(System.currentTimeMillis() - (cnt * 24L * 60L * 60L * 1000L));
        String gregorianDate = date.toString();
        return getJalaliDate(gregorianDate);
    }

    public static String getNextDate(int dayCnt) {
        long cnt = dayCnt;
        Date date = new Date(System.currentTimeMillis() + (cnt * 24L * 60L * 60L * 1000L));
        String gregorianDate = date.toString();
        return getJalaliDate(gregorianDate);
    }

//    public static String getDateToString(String date, int... toChange) {
//        if (toChange.length == 0)
//            toChange = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
//        boolean day = false, month = false, year = false;
//        for (int i : toChange) {
//            if (i == Calendar.YEAR) year = true;
//            else if (i == Calendar.MONTH) month = true;
//            else if (i == Calendar.DAY_OF_MONTH) day = true;
//        }
//        StringTokenizer st = new StringTokenizer(CommonUtils.reverseDate(date), "/");
//        int i = 0;
//        String dateToString = "";
//        String monthString[] = {"فروردين", "ارديبهشت", "خرداد", "تير", "مرداد", "شهريور", "مهر", "آبان", "آذر", "دي", "بهمن", "اسفند"};
//        while (st.hasMoreTokens()) {
//            i++;
//            String tocken = st.nextToken();
//            switch (i) {
//                case 1:
//                    dateToString += " " + (day ? NumberUtils.getAsString(Long.parseLong(tocken)).toString() : tocken);
//                    break;
//                case 2:
//                    int tok = Integer.parseInt(tocken);
//                    String thisMonth = monthString[tok - 1];
//                    dateToString += " " + (month ? thisMonth : tocken);
//                    break;
//                case 3:
//                    dateToString += " " + (year ? NumberUtils.getAsString(Long.parseLong(tocken)).toString() : tocken);
//                    break;
//            }
//        }
//        return dateToString;
//    }

    public static String getTimeDetail() {
        java.util.Date date = new java.util.Date();
        return timeFormatDetail.format(date);
    }

    public static Long getTimeDiff(String time1, String time2) {
        long timeint1 = Integer.valueOf(time1.substring(0, 2)) * 60 + Integer.valueOf(time1.substring(3, 3 + 2)) * 60 + Integer.valueOf(time1.substring(3 + 3, 3 + 3 + 2));
        long timeint2 = Integer.valueOf(time2.substring(0, 2)) * 60 + Integer.valueOf(time2.substring(3, 3 + 2)) * 60 + Integer.valueOf(time2.substring(3 + 3, 3 + 3 + 2));
        return Math.abs(timeint1 - timeint2);
    }

    public static void main(String[] args) {
//        System.out.println("getTime() = " + getTime());
//    System.out.println(getTimeDetail());
        addDaysToJalaliDate("1396/05/01", -1);

        System.out.println(getDateDiff("1390/01/01", "1390/01/31"));
        System.out.println(getDateDiff("1390/01/31", "1390/01/01"));
        System.out.println(getRealDateDiff("1390/01/01", "1390/01/31"));
        System.out.println(getRealDateDiff("1390/01/31", "1390/01/01"));
    }






    public static long getDateDiff2(String firstDate, String secondDate) {

        long fYear = Long.valueOf(firstDate.substring(0, 4));
        long sYear = Long.valueOf(secondDate.substring(0, 4));
        long fMonth = Long.valueOf(firstDate.substring(5, 7));
        long sMonth = Long.valueOf(secondDate.substring(5, 7));
        long fday = Long.valueOf(firstDate.substring(8, 10));
        long sday = Long.valueOf(secondDate.substring(8, 10));

//    if (fday > 30 || (fday == 29 && fMonth == 12))
//        fday = 30;
//    if (sday > 30 || (sday == 29 && sMonth == 12))
//        sday = 30;

        if (fday > 30)
            fday = 30;
        if (sday > 30)
            sday = 30;

        long yearDiff = sYear - fYear;
        long monthDiff = sMonth - fMonth;
        long dayDiff = sday - fday;
        long diff = yearDiff * 360 + monthDiff * 30 + dayDiff;

        return Math.abs(diff);
    }

    public static long getNewBondDateDiff(String firstDate, String secondDate) {

        long fYear = Long.valueOf(firstDate.substring(0, 4));
        long sYear = Long.valueOf(secondDate.substring(0, 4));
        long fMonth = Long.valueOf(firstDate.substring(5, 7));
        long sMonth = Long.valueOf(secondDate.substring(5, 7));
        long fday = Long.valueOf(firstDate.substring(8, 10));
        long sday = Long.valueOf(secondDate.substring(8, 10));

//    if (fday > 30 || (fday == 29 && fMonth == 12))
//        fday = 30;
//    if (sday > 30 || (sday == 29 && sMonth == 12))
//        sday = 30;

        if (fday > 30)
            fday = 30;
        if (sday > 30)
            sday = 30;

        long yearDiff = sYear - fYear;
        long monthDiff = sMonth - fMonth;
        long dayDiff = sday - fday;
        long diff = yearDiff * 360 + monthDiff * 30 + dayDiff;

        return Math.abs(diff);
    }

    public static long getDateDiffWithOutCabise(String firstDate, String secondDate) {
        String fDate = getGregorianDate(firstDate);
        Date fGregorianDate = Date.valueOf(fDate);
        long fTime = fGregorianDate.getTime();

        String sDate = getGregorianDate(secondDate);
        Date sGregorianDate = Date.valueOf(sDate);
        long sTime = sGregorianDate.getTime();

        long diff = Math.abs((fTime - sTime) / (24 * 60 * 60 * 1000));
        if (isLeapJalaliDate(firstDate) && !isLeapJalaliDate(secondDate)) {
            diff -= 1;
        }
        return diff;

    }


    public static String convertGregorianDateToOracle(String gregorianDate) throws ParseException {
        SimpleDateFormat oraclFormat = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = oraclFormat.format(dateFormat.parse(gregorianDate));
        return format;
    }

    public static String toDateDotNetDate(long dotNetDate) {
        if (dotNetDate == 0) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
        long localTAE = TICKS_AT_EPOCH;
        if (milliDiff == 14400000) { //dubai
            localTAE += 1800000 * TICKS_PER_MILLISECOND;
        }
        java.util.Date date = new java.util.Date((dotNetDate - localTAE) / TICKS_PER_MILLISECOND);
        return getJalaliDate(dateFormatDetail.format(date)) + " - " + timeFormat.format(date);
    }

    public static String getPersianDateTime(java.util.Date gregorianDate, String separator) {
        String date = getJalaliDate(convertGregorianDateToString(gregorianDate));
        String time = getTime(gregorianDate);
        return date + separator + time;
    }

    public static String getJalalaiFirstDate(String date)
    {
        long Year = Long.valueOf(date.substring(0, 4));
        return Year+"/01/01";
    }

    public static String convertGregorianDateToString(java.util.Date date) {
        return dateFormatDetail.format(date);
    }

    public static String getTime(java.util.Date date) {
        return timeFormatSecond.format(date);
    }
}
