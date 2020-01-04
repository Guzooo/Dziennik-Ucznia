package pl.Guzooo.DziennikUcznia;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
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

    public static int getWriteOnlyMinutes(int hours, int minutes){
        return hours * 60 + minutes;
    }

    public static String getTimeToReadFromWriteOnlyMinutes(int allMinutes){
        int hours = getHoursFromWriteOnlyMinutes(allMinutes);
        int minutes = getMinutesFromWriteOnlyMinutes(allMinutes);
        return String.format("%02d:%02d", hours, minutes);
    }

    public static int getHoursFromWriteOnlyMinutes(int allMinutes){
        return  allMinutes / 60;
    }

    public static int getMinutesFromWriteOnlyMinutes(int allMinutes){
        return allMinutes % 60;
    }
}