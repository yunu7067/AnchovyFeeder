package com.example.anchovyfeeder.realmdb.utils;

import java.util.Calendar;
import java.util.Date;

public class DateCalculator {
    /**
     * 이번 달의 첫번째와 마지막 날짜의 Date 객체를 반환하는 메소드
     *
     * @return Date[2] {start, end}
     */
    public Date[] getFirstAndLastDayOfMonth() {
        Date[] dates = new Date[2];
        Calendar cal;

        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
        dates[0] = cal.getTime();
        cal.add(Calendar.MONTH, +1);
        cal.add(Calendar.MILLISECOND, -1);
        dates[1] = cal.getTime();

        android.util.Log.i("DateCalculator1", "시작일자" + dates[0] + ", 종료일자" + dates[1]);

        return dates;
    }

    /**
     * 지난 3주, 이번 주, 다음 3주의 시작 날짜와 종료 날짜의 Date 객체를 반환하는 메소드
     *
     * @return  Date[14] {start, end, ... start, end};
     */
    public Date[] getLastAndNext3WeekOfNow() {
        Date[] dates = new Date[14];
        Calendar cal1, cal2;

        cal1 = Calendar.getInstance();
        cal1.set(Calendar.DAY_OF_WEEK, cal1.getFirstDayOfWeek());
        cal1.set(Calendar.HOUR_OF_DAY, cal1.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal1.set(Calendar.MINUTE, cal1.getActualMinimum(Calendar.MINUTE));
        cal1.set(Calendar.SECOND, cal1.getActualMinimum(Calendar.SECOND));
        cal1.set(Calendar.MILLISECOND, cal1.getActualMinimum(Calendar.MILLISECOND));
        cal1.add(Calendar.WEEK_OF_YEAR, -3);// 3주전으로 설정
        cal2 = (Calendar) cal1.clone(); // 복사
        cal2.add(Calendar.DATE, 7);
        cal2.add(Calendar.MILLISECOND, -1);
        for (int i = 0; i < 7; i++) {
            // Date 객체만 뽑아오기
            dates[i * 2] = cal1.getTime();
            dates[i * 2 + 1] = cal2.getTime();
            if (i == 6) break;
            // 7주 더하기
            cal1.add(Calendar.DATE, 7);
            cal2.add(Calendar.DATE, 7);
        }

        android.util.Log.i("DateCalculator2", "시작일자" + dates[0] + ", 종료일자" + dates[1]);
        android.util.Log.i("DateCalculator2", "시작일자" + dates[4] + ", 종료일자" + dates[5]);
        android.util.Log.i("DateCalculator2", "시작일자" + dates[6] + ", 종료일자" + dates[7]);
        android.util.Log.i("DateCalculator2", "시작일자" + dates[12] + ", 종료일자" + dates[13]);

        return dates;
    }

    /**
     * 이번 주를 포함한 지난 7주 간의 시작 날짜와 종료 날짜의 Date 객체를 반환하는 메소드
     *
     * @return  Date[14] {start, end, ... start, end};
     */
    public Date[] getLast7WeeksToDates() {
        Date[] dates = new Date[14];
        Calendar cal1, cal2;

        cal1 = Calendar.getInstance();
        cal1.set(Calendar.DAY_OF_WEEK, cal1.getFirstDayOfWeek());
        cal1.set(Calendar.HOUR_OF_DAY, cal1.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal1.set(Calendar.MINUTE, cal1.getActualMinimum(Calendar.MINUTE));
        cal1.set(Calendar.SECOND, cal1.getActualMinimum(Calendar.SECOND));
        cal1.set(Calendar.MILLISECOND, cal1.getActualMinimum(Calendar.MILLISECOND));
        cal1.add(Calendar.WEEK_OF_YEAR, -6);// 3주전으로 설정
        cal2 = (Calendar) cal1.clone(); // 복사
        cal2.add(Calendar.DATE, 7);
        cal2.add(Calendar.MILLISECOND, -1);
        for (int i = 0; i < 7; i++) {
            // Date 객체만 뽑아오기
            dates[i * 2] = cal1.getTime();
            dates[i * 2 + 1] = cal2.getTime();
            if (i == 6) break;
            // 7주 더하기
            cal1.add(Calendar.DATE, 7);
            cal2.add(Calendar.DATE, 7);
        }

        android.util.Log.i("DateCalculator2", "시작일자" + dates[0] + ", 종료일자" + dates[1]);
        android.util.Log.i("DateCalculator2", "시작일자" + dates[4] + ", 종료일자" + dates[5]);
        android.util.Log.i("DateCalculator2", "시작일자" + dates[6] + ", 종료일자" + dates[7]);
        android.util.Log.i("DateCalculator2", "시작일자" + dates[12] + ", 종료일자" + dates[13]);

        return dates;
    }

}
