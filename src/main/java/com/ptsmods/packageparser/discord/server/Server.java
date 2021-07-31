package com.ptsmods.packageparser.discord.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Server {
    private final long id;
    private final String name;
    private final List<Channel> channels = new ArrayList<>();

    public Server(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public List<Channel> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;
        Server server = (Server) o;
        return getId() == server.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
