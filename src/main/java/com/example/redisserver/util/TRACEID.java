package com.example.redisserver.util;



public class TRACEID
{
    private static final ThreadLocal<String> TRACEID = new ThreadLocal<String>();
    private static final Uid                 uid     = WinterId.instance();

    public static String newTraceId()
    {
        String traceId = uid.generateDigits();
        TRACEID.set(traceId);
        return traceId;
    }

    public static String currentTraceId()
    {
        String result = TRACEID.get();
        if (result == null)
        {
            return newTraceId();
        }
        else
        {
            return result;
        }
    }

    public static void bind(String traceId)
    {
        TRACEID.set(traceId);
    }
}
