package lemury.biletomat.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//mport java.time.format.DateTimeParseException;

public class DateFormatter {
    private final String[] patterns;

    public DateFormatter(String... patterns){
        this.patterns = patterns;
    }

    public LocalDate parse(String text){
        for(int i = 0; i < patterns.length; i++){
            try{
                return LocalDate.parse(text, DateTimeFormatter.ofPattern(patterns[i]));
            }catch(Exception e){}
        }
        throw new IllegalArgumentException("Not able to parse the date for all patterns given");
    }
}