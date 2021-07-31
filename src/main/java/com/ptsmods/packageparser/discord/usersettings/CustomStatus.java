package com.ptsmods.packageparser.discord.usersettings;

import java.util.Date;

public class CustomStatus {
    private final String text;
    private final Date expiresAt;
    private final Long emojiId;
    private final String emojiName;

    public CustomStatus(String text, Date expiresAt, Long emojiId, String emojiName) {
        this.text = text;
        this.expiresAt = expiresAt;
        this.emojiId = emojiId;
        this.emojiName = emojiName;
    }

    public String getText() {
        return text;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public Long getEmojiId() {
        return emojiId;
    }

    public String getEmojiName() {
        return emojiName;
    }
}
