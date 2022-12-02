package com.example.redisserver.command;


import com.example.redisserver.RedisCore;

/**
 * @author WesleyGo
 */
public interface WriteCommand extends Command {
    /**
     * for aof
     * @param redisCore
     */
    void handle(RedisCore redisCore);

}
