package com.example.redisserver.command;



import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;
import com.example.redisserver.resp.SimpleString;
import com.example.redisserver.util.TRACEID;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author WesleyGo
 */
public class CommandFactory
{
    private static final Logger LOGGER = Logger.getLogger(CommandFactory.class);
    static               Map<String, Supplier<Command>> map    = new HashMap<>();

    static
    {
        for (CommandType each : CommandType.values())
        {
            map.put(each.name(), each.getSupplier());
        }
    }

    public static Command from(RespArray arrays)
    {
        //zadd key score1 value1 score2 value2
        Resp[]            array       = arrays.getArray();
        //resp数组第一个即为指令的名称 zadd
        String            commandName = ((BulkString) array[0]).getContent().toUtf8String().toLowerCase();
        //supplier 存储指令类型的类
        Supplier<Command> supplier    = map.get(commandName);
        if (supplier == null)
        {
            LOGGER.debug("traceId:"+ TRACEID.currentTraceId()+" 不支持的命令："+ commandName);
            System.out.println("不支持的命令：" + commandName);
            return null;
        }
        else
        {
            try
            {   //获取指令
                Command command = supplier.get();
                //设置指令内容
                command.setContent(array);
                //返回指令内容
                return command;
            }
            catch (Throwable e)
            {
                LOGGER.debug("traceId:"+TRACEID.currentTraceId()+" 不支持的命令：{},数据读取异常"+commandName);
                e.printStackTrace();
                return null;
            }
        }
    }
    public static Command from(SimpleString string)
    {
        String            commandName = string.getContent().toLowerCase();
        Supplier<Command> supplier    = map.get(commandName);
        if (supplier == null)
        {
            LOGGER.debug("traceId:"+TRACEID.currentTraceId()+" 不支持的命令："+ commandName);
            System.out.println("不支持的命令：" + commandName);
            return null;
        }
        else
        {
            try
            {
                return supplier.get();
            }
            catch (Throwable e)
            {
                LOGGER.debug("traceId:"+TRACEID.currentTraceId()+" 不支持的命令：{},数据读取异常"+commandName);
                e.printStackTrace();
                return null;
            }
        }
    }
}
