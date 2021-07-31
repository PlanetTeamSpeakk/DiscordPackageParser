package com.ptsmods.packageparser.discord.misc;

import com.ptsmods.packageparser.discord.server.Channel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    private final long id;
    private final Date timestamp;
    private final String content;
    private final String contentLowercase;
    private final Channel channel;
    private final String stringRes;

    public Message(long id, String content, Channel channel) {
        this.id = id;
        // Way faster to just use this rather than parsing the time from its string representation.
        // Also fixes messages not being able to be parsed on Java 8.
        this.timestamp = new Date((id >> 22) + 1420070400000L);
        this.content = content;
        contentLowercase = content.toLowerCase(Locale.ROOT);
        this.channel = channel;
        // SimpleDateFormat not static to make thread-safe.
        stringRes = "**" + new SimpleDateFormat("d MMM yyyy HH:mm").format(getTimestamp()) + ":** " + getContent();
    }

    public long getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    // Used for searching
    public String getContentLower() {
        return contentLowercase;
    }

    public Channel getChannel() {
        return channel;
    }

    public String toString() {
        return stringRes;
    }
}
