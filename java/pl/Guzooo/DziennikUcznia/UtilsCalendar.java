package pl.Guzooo.DziennikUcznia;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsCalendar {

    public static String getTodayForWriting(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static String getDateForReading(String dateWrite){
        String[] dateElements = dateWrite.split("/");
        String dateForReading = dateElements[0] + " ";
        dateForReading += getMonthForReading(dateElements[1]) + " ";
        dateForReading += dateElements[2];
        return dateForReading;
    }

    private static String getMonthForReading(String monthWrite){
        int monthNumber = Integer.valueOf(monthWrite) -1;
        DateFormatSymbols symbols = new DateFormatSymbols();
        return symbols.getShortMonths()[monthNumber];
    }
}
