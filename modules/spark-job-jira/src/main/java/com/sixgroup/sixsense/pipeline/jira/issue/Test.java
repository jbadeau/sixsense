package com.sixgroup.sixsense.pipeline.jira.issue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) throws ParseException {

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        Date date = simpleDateFormat.parse("2021-08-17T06:11:53.076+0000");
        System.out.println(date);
    }

}
