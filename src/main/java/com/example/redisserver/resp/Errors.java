package com.example.redisserver.resp;

/**
 * @author WesleyGo
 */
public class Errors implements Resp
{
    String content;

    public Errors(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
}
