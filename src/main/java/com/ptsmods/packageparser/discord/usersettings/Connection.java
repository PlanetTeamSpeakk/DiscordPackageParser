package com.ptsmods.packageparser.discord.usersettings;

public class Connection {
    private final String type;
    private final String id;
    private final String name;
    private final boolean revoked;
    private final int visibility;
    private final boolean friendSync,
                            showActivity,
                            verified;

    public Connection(String type, String id, String name, boolean revoked, int visibility, boolean friendSync, boolean showActivity, boolean verified) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.revoked = revoked;
        this.visibility = visibility;
        this.friendSync = friendSync;
        this.showActivity = showActivity;
        this.verified = verified;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public int getVisibility() {
        return visibility;
    }

    public boolean isFriendSync() {
        return friendSync;
    }

    public boolean isShowActivity() {
        return showActivity;
    }

    public boolean isVerified() {
        return verified;
    }
}
