package com.gamer.data.mpcserver.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamer.data.mpcserver.commands.CommandHandler;
import com.gamer.data.mpcserver.log.Log;

/**
 * 命令执行上下文。
 *
 * <p>用于在各个 {@link CommandHandler} 间共享基础设施对象，
 * 避免到处 new/传参。</p>
 */
public class CommandContext {
    private final ObjectMapper objectMapper;
    private final Log log;
    private final DbDefaults dbDefaults;
    private final FileSandbox fileSandbox;
    private final RedisDefaults redisDefaults;

    /**
     * @param objectMapper JSON 序列化/反序列化工具
     * @param log 日志实现
     */
    public CommandContext(ObjectMapper objectMapper, Log log, DbDefaults dbDefaults, FileSandbox fileSandbox,
        RedisDefaults redisDefaults) {
        this.objectMapper = objectMapper;
        this.log = log;
        this.dbDefaults = dbDefaults;
        this.fileSandbox = fileSandbox;
        this.redisDefaults = redisDefaults;
    }

    /**
     * @return ObjectMapper（非空）
     */
    public ObjectMapper mapper() {
        return objectMapper;
    }

    /**
     * @return Log（非空）
     */
    public Log log() {
        return log;
    }

    public DbDefaults dbDefaults() {
        return dbDefaults;
    }

    public FileSandbox fileSandbox() {
        return fileSandbox;
    }

    /**
     * @return Redis 连接信息（允许为 null；命令侧可自行决定默认值或报错）
     */
    public RedisDefaults redisDefaults() {
        return redisDefaults;
    }
}

