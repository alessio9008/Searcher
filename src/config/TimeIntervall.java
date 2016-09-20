/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import converter.PathConverter;
import converter.TimestampConverter;
import custom.Timestamp;
import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author alessio
 */
@XStreamAlias("timeIntervall")
public class TimeIntervall implements Serializable {

    @XStreamAlias("minTimestamp")
    @XStreamAsAttribute
    @XStreamConverter(TimestampConverter.class)
    protected Calendar minTimestamp;
    @XStreamAlias("maxTimestamp")
    @XStreamAsAttribute
    @XStreamConverter(TimestampConverter.class)
    protected Calendar maxTimestamp;

    public TimeIntervall(Calendar minTimestamp, Calendar maxTimestamp) {
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
    }

    public Calendar getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(Calendar minTimestamp) {
        this.minTimestamp = minTimestamp;
    }

    public Calendar getMaxTimestamp() {
        return maxTimestamp;
    }

    public void setMaxTimestamp(Calendar maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    @Override
    public String toString() {
        return "TimeIntervall{" + "minTimestamp=" + minTimestamp + ", maxTimestamp=" + maxTimestamp + '}';
    }

    

    @Override
    public Object clone() throws CloneNotSupportedException {
        Calendar start = new Timestamp(TimeZone.getTimeZone(Timestamp.TIMEZONE));
        Calendar end = new Timestamp(TimeZone.getTimeZone(Timestamp.TIMEZONE));
        start.setTimeInMillis(minTimestamp.getTimeInMillis());
        end.setTimeInMillis(maxTimestamp.getTimeInMillis());
        return new TimeIntervall(start, end);
    }

}
