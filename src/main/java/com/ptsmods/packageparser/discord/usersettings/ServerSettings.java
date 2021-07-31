package com.ptsmods.packageparser.discord.usersettings;

import java.util.Date;
import java.util.List;

public class ServerSettings {
    private final Long id;
    private final boolean suppressEveryone, suppressRoles;
    private final int messageNotifications;
    private final boolean mobilePush;
    private final boolean muted;
    private final Date muteEnd;
    private final boolean hideMutedChannels;
    private final List<ChannelOverride> channelOverrides;
    private final int version;

    public ServerSettings(Long id, boolean suppressEveryone, boolean suppressRoles, int messageNotifications, boolean mobilePush, boolean muted, Date muteEnd, boolean hideMutedChannels, List<ChannelOverride> channelOverrides, int version) {
        this.id = id;
        this.suppressEveryone = suppressEveryone;
        this.suppressRoles = suppressRoles;
        this.messageNotifications = messageNotifications;
        this.mobilePush = mobilePush;
        this.muted = muted;
        this.muteEnd = muteEnd;
        this.hideMutedChannels = hideMutedChannels;
        this.channelOverrides = channelOverrides;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public boolean isSuppressEveryone() {
        return suppressEveryone;
    }

    public boolean isSuppressRoles() {
        return suppressRoles;
    }

    public int getMessageNotifications() {
        return messageNotifications;
    }

    public boolean isMobilePush() {
        return mobilePush;
    }

    public boolean isMuted() {
        return muted;
    }

    public Date getMuteEnd() {
        return muteEnd;
    }

    public boolean isHideMutedChannels() {
        return hideMutedChannels;
    }

    public List<ChannelOverride> getChannelOverrides() {
        return channelOverrides;
    }

    public int getVersion() {
        return version;
    }

    public static class ChannelOverride {
        private final long channelId;
        private final int messageNotifications;
        private final boolean muted;
        private final Date muteEnd;
        private final boolean collapsed;

        public ChannelOverride(long channelId, int messageNotifications, boolean muted, Date muteEnd, boolean collapsed) {
            this.channelId = channelId;
            this.messageNotifications = messageNotifications;
            this.muted = muted;
            this.muteEnd = muteEnd;
            this.collapsed = collapsed;
        }

        public long getChannelId() {
            return channelId;
        }

        public int getMessageNotifications() {
            return messageNotifications;
        }

        public boolean isMuted() {
            return muted;
        }

        public Date getMuteEnd() {
            return muteEnd;
        }

        public boolean isCollapsed() {
            return collapsed;
        }
    }
}
