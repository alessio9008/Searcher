/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import custom.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author alessio
 */
public class TimestampConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        try {
            if (type != null) {
                return Calendar.class.isAssignableFrom(type);
            }
            else{
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Object fromString(String string) {
        try {
            if (string != null) {
                Date date = new SimpleDateFormat(Timestamp.SDF_FORMAT).parse(string);
                if (date != null) {
                    Calendar calendar = new Timestamp(TimeZone.getTimeZone(Timestamp.TIMEZONE));
                    calendar.setTime(date);
                    return calendar;
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String toString(Object obj) {
        try {
            if (obj instanceof Calendar) {
                return new SimpleDateFormat(Timestamp.SDF_FORMAT).format(Calendar.class.cast(obj).getTime());
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

}
