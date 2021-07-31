package com.ptsmods.packageparser.discord.server;

import com.ptsmods.packageparser.discord.misc.Message;
import com.ptsmods.packageparser.discord.user.UserData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Channel {
    private final long id;
    private final Type type;
    private String name;
    private final Server server;
    private final List<Message> messages = new ArrayList<>();
    private final Long recipientId;

    public Channel(long id, Type type, String name, Server server, Long recipientId, Runnable onFinish) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.server = server;
        this.recipientId = recipientId;
        if (recipientId != null) {
            this.name = "DMs with " + recipientId;
            UserData.getUserData(recipientId).thenAccept(data -> {
                this.name = "DMs with " + data.username + "#" + data.discriminator;
                if (onFinish != null) onFinish.run();
            });
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Server getServer() {
        return server;
    }

    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);
    }

    public List<Message> getMessages() {
        messages.sort(Comparator.comparingLong(msg -> msg.getTimestamp().getTime()));
        return messages;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type {
        GUILD, PRIVATE, UNKNOWN, GROUP, IDK, IDK_EITHER // What other types of text channels do you have?
    }
}
