package com.ptsmods.packageparser.discord.usersettings;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class Application {
    private final long id;
    private final String name;
    private final Image icon;
    private final String description;
    private final String summary;
    private final boolean hook;
    private final boolean botPublic;
    private final boolean botRequireCodeGrant;
    private final String verifyKey;
    private final int flags;
    private final String secret;
    private final List<String> redirectUris;
    private final int rpcApplicationState;
    private final int storeApplicationState;
    private final int verificationState;
    private final String interactionsEndpointUrl;
    private final boolean integrationPublic;
    private final boolean integrationRequireCodeGrant;
    private final Bot bot;

    public Application(long id, String name, Image icon, String description, String summary, boolean hook, boolean botPublic, boolean botRequireCodeGrant, String verifyKey, int flags, String secret, List<String> redirectUris, int rpcApplicationState, int storeApplicationState, int verificationState, String interactionsEndpointUrl, boolean integrationPublic, boolean integrationRequireCodeGrant, Bot bot) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.summary = summary;
        this.hook = hook;
        this.botPublic = botPublic;
        this.botRequireCodeGrant = botRequireCodeGrant;
        this.verifyKey = verifyKey;
        this.flags = flags;
        this.secret = secret;
        this.redirectUris = Collections.unmodifiableList(redirectUris);
        this.rpcApplicationState = rpcApplicationState;
        this.storeApplicationState = storeApplicationState;
        this.verificationState = verificationState;
        this.interactionsEndpointUrl = interactionsEndpointUrl;
        this.integrationPublic = integrationPublic;
        this.integrationRequireCodeGrant = integrationRequireCodeGrant;
        this.bot = bot;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getSummary() {
        return summary;
    }

    public boolean isHook() {
        return hook;
    }

    public boolean isBotPublic() {
        return botPublic;
    }

    public boolean doesBotRequireCodeGrant() {
        return botRequireCodeGrant;
    }

    public String getVerifyKey() {
        return verifyKey;
    }

    public int getFlags() {
        return flags;
    }

    public String getSecret() {
        return secret;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public int getRpcApplicationState() {
        return rpcApplicationState;
    }

    public int getStoreApplicationState() {
        return storeApplicationState;
    }

    public int getVerificationState() {
        return verificationState;
    }

    public String getInteractionsEndpointUrl() {
        return interactionsEndpointUrl;
    }

    public boolean isIntegrationPublic() {
        return integrationPublic;
    }

    public boolean doesIntegrationRequireCodeGrant() {
        return integrationRequireCodeGrant;
    }

    public Bot getBot() {
        return bot;
    }

    public static class Bot {
        private final long id;
        private final String username;
        private final Image avatar;
        private final int discriminator;
        private final int publicFlags;
        private final boolean bot;
        private final String token;

        public Bot(long id, String username, Image avatar, int discriminator, int publicFlags, boolean bot, String token) {
            this.id = id;
            this.username = username;
            this.avatar = avatar;
            this.discriminator = discriminator;
            this.publicFlags = publicFlags;
            this.bot = bot;
            this.token = token;
        }

        public long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public Image getAvatar() {
            return avatar;
        }

        public int getDiscriminator() {
            return discriminator;
        }

        public int getPublicFlags() {
            return publicFlags;
        }

        public boolean isBot() {
            return bot;
        }

        public String getToken() {
            return token;
        }
    }
}
