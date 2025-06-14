package com.dam.commons.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.StringTokenizer;

public class BaseDateUtils {
    public static final int[] g_days_in_month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int[] j_days_in_month = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
    private static String[] days = {
            "یکشنبه",
            "دوشنبه",
            "سه شنبه",
            "چهارشنبه",
            "پنج شنبه",
            "جمعه",
            "شنبه",
    };

    public static final SimpleDateFormat timeFormatCompact = new SimpleDateFormat("HHmmss");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat timeFormatSecond = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat timeFormatDetail = new SimpleDateFormat("HH:mm:ss:SSS");
    public static final SimpleDateFormat dateFormatDetail = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat dateTimeFormatFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final long TICKS_AT_EPOCH = 621355968000000000L;
    public static final long TICKS_PER_MILLISECOND = 10000;

    public static final String getJalaliDate(String gregorianDate) {
        ///
        int g_y = Integer.parseInt(gregorianDate.substring(0, 4));
        int g_m = Integer.parseInt(gregorianDate.substring(5, 7));
        int g_d = Integer.parseInt(gregorianDate.substring(8));
        ///
        int gy, gm, gd;
        int jy, jm, jd;
        long g_day_no, j_day_no;
        int j_np;

        int i;
        gy = g_y - 1600;
        gm = g_m - 1;
        gd = g_d - 1;

        g_day_no = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400;
        for (i = 0; i < gm; ++i)
            g_day_no += g_days_in_month[i];
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)))
            /* leap and after Feb */
            ++g_day_no;
        g_day_no += gd;

        j_day_no = g_day_no - 79;

        j_np = (int) j_day_no / 12053;

        j_day_no %= 12053;

        jy = (int) (979 + 33 * j_np + 4 * (j_day_no / 1461));
        j_day_no %= 1461;

        if (j_day_no >= 366) {
            jy += (j_day_no - 1) / 365;
            j_day_no = (j_day_no - 1) % 365;
        }

        for (i = 0; i < 11 && j_day_no >= j_days_in_month[i]; ++i) {
            j_day_no -= j_days_in_month[i];
        }
        jm = i + 1;
        jd = (int) j_day_no + 1;

        return jy + "/" + (jm < 10 ? "0" + jm : "" + jm) + "/" + (jd < 10 ? "0" + jd : "" + jd);
    }

    public static final boolean isValidJalaliDate(String jalaliDate) {
        boolean valid = true;
        try {
            valid &= (jalaliDate.length() == 10);
            valid &= (jalaliDate.charAt(4) == '/');
            valid &= (jalaliDate.charAt(7) == '/');

            int j_y = Integer.parseInt(jalaliDate.substring(0, 4));
            int j_m = Integer.parseInt(jalaliDate.substring(5, 7));
            int j_d = Integer.parseInt(jalaliDate.substring(8));

            valid &= (j_y > 1200 && j_y < 1500);
            valid &= (j_m >= 1 && j_m <= 12);
            valid &= ((j_m >= 1 && j_m <= 6) && (j_d >= 1 && j_d <= 31)) || ((j_m >= 7) && (j_d >= 1 && j_d <= 30));
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    public static final boolean isValidTime(String time) {
        boolean valid = true;
        try {
            valid &= (time.length() == 5);
            valid &= (time.charAt(2) == ':');

            int hh = Integer.parseInt(time.substring(0, 2));
            int mm = Integer.parseInt(time.substring(3));

            valid &= (hh >= 0 && hh < 24);
            valid &= (mm >= 0 && mm < 60);
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    public static final java.util.Date convertGregorianDate(String gregorianDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(gregorianDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public static final String getTodayJalali() {
        Date date = new Date(System.currentTimeMillis());
        String gregorianDate = date.toString();
        return getJalaliDate(gregorianDate);
    }

    public static final String getGregorianDate(String jalaliDate) {
        ///
        int j_y = Integer.parseInt(jalaliDate.substring(0, 4));
        int j_m = Integer.parseInt(jalaliDate.substring(5, 7));
        int j_d = Integer.parseInt(jalaliDate.substring(8));
        ///
        int gy, gm, gd;
        int jy, jm, jd;
        long g_day_no, j_day_no;
        int leap;

        int i;

        jy = j_y - 979;
        jm = j_m - 1;
        jd = j_d - 1;

        j_day_no = 365 * jy + (jy / 33) * 8 + (jy % 33 + 3) / 4;
        for (i = 0; i < jm; ++i)
            j_day_no += j_days_in_month[i];

        j_day_no += jd;

        g_day_no = j_day_no + 79;

        gy = (int) (1600 + 400 * (g_day_no / 146097)); /* 146097 = 365*400 + 400/4 - 400/100 + 400/400 */
        g_day_no = g_day_no % 146097;

        leap = 1;
        if (g_day_no >= 36525) /* 36525 = 365*100 + 100/4 */ {
            g_day_no--;
            gy += 100 * (g_day_no / 36524); /* 36524 = 365*100 + 100/4 - 100/100 */
            g_day_no = g_day_no % 36524;

            if (g_day_no >= 365)
                g_day_no++;
            else
                leap = 0;
        }

        gy += 4 * (g_day_no / 1461); /* 1461 = 365*4 + 4/4 */
        g_day_no %= 1461;

        if (g_day_no >= 366) {
            leap = 0;

            g_day_no--;
            gy += g_day_no / 365;
            g_day_no = g_day_no % 365;
        }

        for (i = 0; g_day_no >= g_days_in_month[i] + ((i == 1 && leap == 1) ? 1 : 0); i++)
            g_day_no -= g_days_in_month[i] + ((i == 1 && leap == 1) ? 1 : 0);
        gm = i + 1;
        gd = (int) g_day_no + 1;

//        return gy + "-" + gm + "-" + gd;
        return gy + "-" + (gm < 10 ? "0" + gm : "" + gm) + "-" + (gd < 10 ? "0" + gd : "" + gd);
    }

    public static final int getDayOfWeekIndexJalali(String jDate) {
        String gDate = getGregorianDate(jDate);
        Date d = Date.valueOf(gDate);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int i = c.get(Calendar.DAY_OF_WEEK);
        return i - 1;
    }

    public static final String getDayOfWeekJalali(String jDate) {
        return days[getDayOfWeekIndexJalali(jDate)];
    }

    public static final String addDaysToJalaliDate(String jalaliDate, int days) {
        if (days == 0)
            return jalaliDate;
        String gDate = getGregorianDate(jalaliDate);
        Date d = Date.valueOf(gDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        gDate = new Date(calendar.getTimeInMillis()).toString();
        String jDate = getJalaliDate(gDate);
        return jDate;
//------------------ for xml
//        String month="";
//        String day ="";
//        String year ="";
//        String jDate="";
//        if(jalaliDate.contains ("/"))
//        {
//            String parts[] = jalaliDate.split("[/]");
//            year =  parts[0]; // i want to strip part after  +
//            month =  parts[1]; // i want to strip part after  +
//            day =  parts[2]; // i want to strip part after  +
//        }
//        if( month.equals("06") && day.equals("30")){
//            jDate=year+"/06/31";
//        }else{
//            String gDate = getGregorianDate(jalaliDate);
//            Date d = Date.valueOf(gDate);
//            long time = d.getTime();
//            time += days * 24l * 60l * 60l * 1000l;
//            d = new Date(time);
//            gDate = d.toString();
//            jDate = getJalaliDate(gDate);}
//        return jDate;
    }

    public static final String addMonthsToJalaliDate(String jalaliDate, int addedMonth) {
        if (addedMonth == 0) {
            return jalaliDate;
        } else if (addedMonth < 0) {
            throw new RuntimeException("Not support reverse mode yet");
        }
        StringTokenizer gDate = new StringTokenizer(jalaliDate, "/");
        int year = Integer.parseInt(gDate.nextToken());
        int month = Integer.parseInt(gDate.nextToken());
        String day = gDate.nextToken();

        month += addedMonth;
        int mod = month % 12;
        int addedYear = month / 12;
        if (mod == 0) {
            if (addedYear > 1) {
                year += addedYear - 1;
                month = 12;
            }
        } else {
            year += addedYear;
            month = mod;
        }
        String monthStr = month < 10 ? "0" + month : month + "";
        return year + "/" + monthStr + "/" + day;
    }

    public static final String addYearsToJalaliDate(String jalaliDate, int addedYear) {
        if (addedYear == 0)
            return jalaliDate;
        StringTokenizer gDate = new StringTokenizer(jalaliDate, "/");
        int year = Integer.parseInt(gDate.nextToken());
        String month = gDate.nextToken();
        String day = gDate.nextToken();

        return (year + addedYear) + "/" + month + "/" + day;
    }

    public static final boolean isLeapJalaliDate(String date) {
        int g_y = Integer.parseInt(date.substring(0, 4));
        int gy = g_y - 979;
        return (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0);

    }

    public static final long getDateDiff(String firstDate, String secondDate) {
        String fDate = getGregorianDate(firstDate);
        Date fGregorianDate = Date.valueOf(fDate);
        long fTime = fGregorianDate.getTime();

        String sDate = getGregorianDate(secondDate);
        Date sGregorianDate = Date.valueOf(sDate);
        long sTime = sGregorianDate.getTime();

        return (Math.abs(fTime - sTime) + 2 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000);

    }

    public static final long getRealDateDiff(String firstDate, String secondDate) {
        String fDate = getGregorianDate(firstDate);
        Date fGregorianDate = Date.valueOf(fDate);
        long fTime = fGregorianDate.getTime();

        String sDate = getGregorianDate(secondDate);
        Date sGregorianDate = Date.valueOf(sDate);
        long sTime = sGregorianDate.getTime();

        if (fTime < sTime) {
            fTime -= 2 * 60 * 60 * 1000;
            sTime += 2 * 60 * 60 * 1000;
        } else {
            fTime += 2 * 60 * 60 * 1000;
            sTime -= 2 * 60 * 60 * 1000;
        }

        return (fTime - sTime) / (24 * 60 * 60 * 1000);

    }

    public static final int getLeapYearCount(String startDate, String endDate) {
        int count = 0;
        while (String.valueOf(startDate).substring(0, 4).compareTo(String.valueOf(endDate).substring(0, 4)) < 0) {
            Long fYear = Long.valueOf(startDate.substring(0, 4));
            if (isLeapJalaliDate(startDate)) {
                count++;
            }
            fYear++;
            startDate = String.valueOf(fYear).substring(0, 4) + startDate.substring(4);
        }
        return count;
    }

    public static final String[] getSeasonInterval(String date) {
        String[] result = {"", ""};
        int startMonth, endMonth;

        int firstIndexOfSlash = date.indexOf('/');
        int secondIndexOfSlash = date.indexOf('/', (firstIndexOfSlash + 1));

        String year = date.substring(0, firstIndexOfSlash);
        int month = Integer.parseInt(date.substring((firstIndexOfSlash + 1), secondIndexOfSlash));
        String day = date.substring(secondIndexOfSlash + 1, date.length());

        int remain = month % 3;
        if (remain == 0) {
            endMonth = month;
            startMonth = month - 2;
        } else if (remain == 1) {
            startMonth = month;
            endMonth = month + 2;
        } else {
            startMonth = month - 1;
            endMonth = month + 1;
        }

        String monthStr = startMonth < 10 ? "0" + startMonth : startMonth + "";
        String[] startInterval = getMonthInterval(year + "/" + monthStr + "/" + day);

        monthStr = endMonth < 10 ? "0" + endMonth : endMonth + "";
        String[] endInterval = getMonthInterval(year + "/" + monthStr + "/" + day);

        result[0] = startInterval[0];
        result[1] = endInterval[1];

        return result;
    }

    public static final String[] getMonthInterval(String date) {

        String[] result = {"", ""};
        int startDay;
        int endDay;

        int firstIndexOfSlash = date.indexOf('/');
        int secondIndexOfSlash = date.indexOf('/', firstIndexOfSlash + 1);

        int year = Integer.parseInt(date.substring(0, firstIndexOfSlash));
        int month = Integer.parseInt(date.substring((firstIndexOfSlash + 1), secondIndexOfSlash));

        startDay = 1;
        if (month >= 0 && month <= 6) {
            endDay = 31;
        } else if (month >= 7 && month <= 11) {
            endDay = 30;
        } else {
            endDay = 29;
        }

        String monthStr = month < 10 ? "0" + month : month + "";
        String startDayStr = startDay < 10 ? "0" + startDay : startDay + "";
        String endDayStr = endDay < 10 ? "0" + endDay : endDay + "";

        result[0] = year + "/" + monthStr + "/" + startDayStr;
        result[1] = year + "/" + monthStr + "/" + endDayStr;

        return result;
    }

    public static final String[] getCurrentFundPeriodDateRange(String startPeriodDate, String today) {
        String[] dateRange = new String[2];
        String startRange = startPeriodDate;
        String endRange = addYearsToJalaliDate(startRange, 1);
        // if today is not in range
        while (endRange.compareTo(today) <= 0) {
            startRange = endRange;
            endRange = addYearsToJalaliDate(startRange, 1);
        }
        dateRange[0] = startRange;
        dateRange[1] = addDaysToJalaliDate(endRange, -1);
        return dateRange;
    }

    public static String getTime() {
        return timeFormat.format(new java.util.Date());
    }

    public static String getTimeCompact() {
        return timeFormatCompact.format(new java.util.Date());
    }

    public static String getTimeSecond() {
        return timeFormatSecond.format(new java.util.Date());
    }

    public static String getDateTimeFull() {
        return dateTimeFormatFull.format(new java.util.Date());
    }

    public static long getJalaliDateEpoch(String jalaliDate) {
        return convertGregorianDate(getGregorianDate(jalaliDate)).getTime();
    }

    public static long getTimeEpoch() {
        return System.currentTimeMillis();
    }

    public static long getTimeEpoch(String time) {
        return LocalDate.now().atTime(LocalTime.parse(time)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    public static String shiftDateBack(String inputDate) {
        // محاسبه فاصله امروز تا تاریخ مرجع
        int daysDiff = Math.abs( NumberUtils.integerValue(getDateDiff("1403/09/05", inputDate)))*-1;
        return  DateUtils.addDaysToJalaliDate(inputDate, daysDiff);
    }
}
