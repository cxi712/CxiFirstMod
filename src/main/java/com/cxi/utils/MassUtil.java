package com.cxi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MassUtil {
    public MassUtil() {}
    public static String getNowTime()
	{
		return getNowTime("yyyy年MM月dd日 HH:mm:ss");
	}
	public static String getNowTime(String format)
	{
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		return df.format(calendar.getTime());
	}
    
}
