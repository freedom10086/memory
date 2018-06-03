package com.tencent.memory.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JsonDateSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        FriendlyDate date = new FriendlyDate(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        jsonGenerator.writeString(date.toFriendlyDate(false));
    }


    public static class FriendlyDate extends Date {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public FriendlyDate(long date) {
            super(date);
        }

        public FriendlyDate() {
            super(System.currentTimeMillis());
        }

        // 默认时间格式
        private String dateFormat = "yyyy年MM月dd日";

        // 月份+日期
        private String nowDateFormat = "MM月dd日";

        // 小时+分钟
        private String timeFormat = " HH:mm";

        // 天数时差
        private String pattern_DayAgo = "{0}天之前";

        // 小时时差
        private String pattern_HoursAgo = "{0}小时之前";

        // 分钟时差
        private String pattern_MinutelAgo = "{0}分钟之前";

        // 秒时差
        private String pattern_SecondsAgo = "{0}秒之前";

        /**
         * 转换成用户所在时区的时间，并返回站点设置的日期格式,返回较为友好的时间格式
         *
         * <pre>
         * 如：
         * <code>6天之前 21:23</code>
         * <code>前天 21:23</code>
         * <code>3小时之前</code>
         * <code>现在</code>
         * </pre>
         */
        public String toFriendlyDate(boolean showTime) {
            try {
                Date now = new Date(); // 现在时间
                String value = "{0}";
                if (showTime) {
                    value = "{0}" + format(timeFormat);
                }

                // 求时间差
                TimeSpan timeSpan = new TimeSpan(this, now);
                if (getTime() > now.getTime()) {
                    return MessageFormat.format(value, format(dateFormat));
                }

                // 时间差超过7天，年份为当年显示月-日 年份不为同一年显示年-月-日
                if (timeSpan.days > 7) {
                    if (this.getYear() == now.getYear()) {
                        return MessageFormat.format(value, format(nowDateFormat));
                    } else {
                        return MessageFormat.format(value, format(dateFormat));
                    }
                }

                // 天数相差大于3天
                if (timeSpan.days >= 3) {
                    String timeScope = MessageFormat.format(pattern_DayAgo,
                            timeSpan.days);
                    return MessageFormat.format(value, timeScope);
                }

                if (timeSpan.days == 2) {
                    return MessageFormat.format(value, "前天");
                }

                if (timeSpan.days == 1) {
                    return MessageFormat.format(value, "昨天");
                }
                if (timeSpan.hours >= 1) {
                    return MessageFormat.format(pattern_HoursAgo, timeSpan.hours);
                }

                if (timeSpan.minutes > 30) {
                    return "半小时前";
                }

                if (timeSpan.minutes >= 1) {
                    return MessageFormat.format(pattern_MinutelAgo,
                            timeSpan.minutes);
                }
                if (timeSpan.seconds >= 1) {
                    return MessageFormat.format(pattern_SecondsAgo,
                            timeSpan.seconds);
                }
                return "刚刚";
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         * 格式化时间
         */
        private String format(String pattern) {
            return new SimpleDateFormat(pattern).format(this);
        }


    }

    // 用于统计时间差
    private static class TimeSpan {
        private static int DAY_STAMP = 86400000;

        private static int HOUR_STAMP = 3600000;

        private static int MINUTES_STAMP = 60000;

        private static int SECONDS_STAMP = 1000;

        // 相差天数
        private int days;

        // 相差小时数
        private int hours;

        // 相差分数
        private int minutes;

        // 相差秒数
        private int seconds;

        private TimeSpan(Date date1, Date date2) {
            long diff = Math.abs(date1.getTime() - date2.getTime());
            days = Long.valueOf(diff / DAY_STAMP).intValue();
            hours = Long.valueOf(diff / HOUR_STAMP % 24).intValue();
            minutes = Long.valueOf(diff / MINUTES_STAMP % 60).intValue();
            seconds = Long.valueOf(diff / SECONDS_STAMP % 60).intValue();
        }
    }

}
