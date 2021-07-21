package io.confluent.connect.jira.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.kafka.connect.errors.ConnectException;

public class DateUtils {
    private static final String JIRA_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String JIRA_ZONED_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static final String X_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    public static String toJiraDateFormat(String isoDateTime) {
        DateTimeFormatter tzFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        LocalDateTime localDateTime = ZonedDateTime.from(tzFormatter.parse(isoDateTime)).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(localDateTime);
    }

    public static Date getJiraDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new ConnectException("Exception occurred while converting String to date ", e);
        }
    }

    public static LocalDateTime getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return ZonedDateTime.from(formatter.parse(date)).toLocalDateTime();
    }

    public static String getJiraDateAsString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }
}
