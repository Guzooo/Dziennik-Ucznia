package pl.Guzooo.DziennikUcznia;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilsCalendar {

    public static String getTodayToWrite(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static String getDateToRead(String dateWrite){
        String[] dateElements = dateWrite.split("/");
        String dateForReading = dateElements[0] + " ";
        dateForReading += getMonthToRead(dateElements[1]) + " ";
        dateForReading += dateElements[2];
        return dateForReading;
    }

    private static String getMonthToRead(String monthWrite){
        int monthNumber = Integer.valueOf(monthWrite) -1;
        DateFormatSymbols symbols = new DateFormatSymbols();
        return symbols.getShortMonths()[monthNumber];
    }

    public static int getTodaysDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayOfWeek(int day, Context context){
        switch (day){
            case Calendar.MONDAY:
                return context.getString(R.string.monday);
            case Calendar.TUESDAY:
                return context.getString(R.string.tuesday);
            case Calendar.WEDNESDAY:
                return context.getString(R.string.wednesday);
            case Calendar.THURSDAY:
                return context.getString(R.string.thursday);
            case Calendar.FRIDAY:
                return context.getString(R.string.friday);
            case Calendar.SATURDAY:
                return context.getString(R.string.saturday);
            case Calendar.SUNDAY:
                return context.getString(R.string.sunday);
            default:
                return "";
        }
    }

    public static int getWriteOnlyMinutes(int hours, int minutes){
        return hours * 60 + minutes;
    }

    public static String getTimeToReadFromWriteOnlyMinutes(int allMinutes){
        int hours = getHoursFromWriteOnlyMinutes(allMinutes);
        int minutes = getMinutesFromWriteOnlyMinutes(allMinutes);
        return getTimeToRead(hours, minutes);
    }

    public static int getHoursFromWriteOnlyMinutes(int allMinutes){
        return  allMinutes / 60;
    }

    public static int getMinutesFromWriteOnlyMinutes(int allMinutes){
        return allMinutes % 60;
    }

    public static String getTimeToRead(int hours, int minutes){
        return String.format("%02d:%02d", hours, minutes);
    }

    public static int[] getTimeToOperating(String timeRead){
        String[] timeStr = timeRead.split(":");
        return new int[]{Integer.parseInt(timeStr[0]),
                        Integer.parseInt((timeStr[1]))};
    }
}