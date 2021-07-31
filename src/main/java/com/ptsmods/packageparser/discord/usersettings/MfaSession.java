package com.ptsmods.packageparser.discord.usersettings;

import java.util.Date;

public class MfaSession {
    private final long userId;
    private final Date started;
    private final String ip;
    private final Date end;
    private final String id;

    public MfaSession(long userId, Date started, String ip, Date end, String id) {
        this.userId = userId;
        this.started = started;
        this.ip = ip;
        this.end = end;
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public Date getStarted() {
        return started;
    }

    public String getIp() {
        return ip;
    }

    public Date getEnd() {
        return end;
    }

    public String getId() {
        return id;
    }
}
