/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custom;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author alessio
 */
public class Timestamp extends GregorianCalendar {

    public static final String SDF_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TIMEZONE="Europe/Rome";

    public Timestamp() {
    }

    public Timestamp(TimeZone zone) {
        super(zone);
    }

    public Timestamp(Locale aLocale) {
        super(aLocale);
    }

    public Timestamp(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }

    public Timestamp(int year, int month, int dayOfMonth) {
        super(year, month, dayOfMonth);
    }

    public Timestamp(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        super(year, month, dayOfMonth, hourOfDay, minute);
    }

    public Timestamp(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        super(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat(SDF_FORMAT).format(this.getTime());
    }

}
