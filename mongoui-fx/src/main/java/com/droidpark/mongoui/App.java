package com.droidpark.mongoui;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
    	SimpleDateFormat format = new SimpleDateFormat("d:M:yyyy H:s:m:S");
        System.out.println(format.format(new Date()));
    }
}
