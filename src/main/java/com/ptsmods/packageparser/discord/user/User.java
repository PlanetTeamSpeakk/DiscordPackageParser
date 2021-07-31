package com.ptsmods.packageparser.discord.user;

import com.ptsmods.packageparser.discord.misc.LibraryApplication;
import com.ptsmods.packageparser.discord.misc.Payment;
import com.ptsmods.packageparser.discord.usersettings.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User {
    private final long id;
    private final String username;
    private final int discriminator;
    private final String email;
    private final boolean verified;
    private final String avatarHash;
    private final boolean hasMobile;
    private final boolean needsEmailVerification;
    private final Date premiumUntil;
    private final int flags;
    private final String phone;
    private final Date tempBannedUntil;
    private final String ip;
    private final Settings settings;
    private final List<Connection> connections;
    private final List<MfaSession> mfaSessions;
    private final List<Relationship> relationships;
    private final List<Payment> payments;
    private final List<Payment.PaymentSource> paymentSources;
    private final List<ServerSettings> guildSettings;
    private final List<LibraryApplication> libraryApplications;
    private final Map<Long, String> notes;

    public User(long id, String username, int discriminator, String email, boolean verified, String avatarHash, boolean hasMobile, boolean needsEmailVerification, Date premiumUntil, int flags, String phone, Date tempBannedUntil, String ip, Settings settings, List<Connection> connections, List<MfaSession> mfaSessions, List<Relationship> relationships, List<Payment> payments, List<Payment.PaymentSource> paymentSources, List<ServerSettings> guildSettings, List<LibraryApplication> libraryApplications, Map<Long, String> notes) {
        this.id = id;
        this.username = username;
        this.discriminator = discriminator;
        this.email = email;
        this.verified = verified;
        this.avatarHash = avatarHash;
        this.hasMobile = hasMobile;
        this.needsEmailVerification = needsEmailVerification;
        this.premiumUntil = premiumUntil;
        this.flags = flags;
        this.phone = phone;
        this.tempBannedUntil = tempBannedUntil;
        this.ip = ip;
        this.settings = settings;
        this.connections = connections;
        this.mfaSessions = mfaSessions;
        this.relationships = relationships;
        this.payments = payments;
        this.paymentSources = paymentSources;
        this.guildSettings = guildSettings;
        this.libraryApplications = libraryApplications;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTag() {
        return getUsername() + '#' + String.format("%04d", getDiscriminator());
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedAt() {
        return new Date((getId() >> 22) + 1420070400000L);
    }

    public boolean isVerified() {
        return verified;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public String getAvatarURL() {
        return "https://cdn.discordapp.com/" + (getAvatarHash() != null ? "avatars/" + getId() + '/' + getAvatarHash() + '.' + (getAvatarHash().startsWith("a_") ? "gif" : "png") : "embed/avatars/" + (getDiscriminator() % 5) + ".png");
    }

    public boolean isHasMobile() {
        return hasMobile;
    }

    public boolean isNeedsEmailVerification() {
        return needsEmailVerification;
    }

    public Date getPremiumUntil() {
        return premiumUntil;
    }

    public int getFlags() {
        return flags;
    }

    public String getPhone() {
        return phone;
    }

    public Date getTempBannedUntil() {
        return tempBannedUntil;
    }

    public String getIp() {
        return ip;
    }

    public Settings getSettings() {
        return settings;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public List<MfaSession> getMfaSessions() {
        return mfaSessions;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public List<Payment.PaymentSource> getPaymentSources() {
        return paymentSources;
    }

    public List<ServerSettings> getGuildSettings() {
        return guildSettings;
    }

    public List<LibraryApplication> getLibraryApplications() {
        return libraryApplications;
    }

    public Map<Long, String> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", discriminator=" + discriminator +
                ", email='" + email + '\'' +
                ", verified=" + verified +
                ", avatarHash='" + avatarHash + '\'' +
                ", hasMobile=" + hasMobile +
                ", needsEmailVerification=" + needsEmailVerification +
                ", premiumUntil=" + premiumUntil +
                ", flags=" + flags +
                ", phone='" + phone + '\'' +
                ", tempBannedUntil=" + tempBannedUntil +
                ", ip='" + ip + '\'' +
                ", settings=" + settings +
                ", connections=" + connections +
                ", mfaSessions=" + mfaSessions +
                ", relationships=" + relationships +
                ", payments=" + payments +
                ", paymentSources=" + paymentSources +
                ", guildSettings=" + guildSettings +
                ", libraryApplications=" + libraryApplications +
                ", notes=" + notes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static class Settings {
        private final String locale;
        private final boolean showCurrentGame;
        private final List<Long> restrictedGuilds;
        private final boolean defaultGuildsRestricted,
                                inlineAttachmentMedia,
                                inlineEmbedMedia,
                                gifAutoPlay,
                                renderEmbeds,
                                renderReactions,
                                animateEmoji,
                                enableTtsCommand,
                                messageDisplayCompact,
                                convertEmoticons;
        private final ContentFilter explicitContentFilter;
        private final boolean disableGamesTab;
        private final Theme theme;
        private final boolean developerMode;
        private final List<Long> guildPositions;
        private final boolean detectPlatformAccounts;
        private final Status status;
        private final int afkTimeout;
        private final int timezoneOffset;
        private final boolean streamNotificationsEnabled,
                                allowAccessibilityDetection,
                                contactSyncEnabled,
                                nativePhoneIntegrationEnabled;
        private final int animateStickers;
        private final int friendDiscoveryFlags;
        private final List<ServerFolder> guildFolders;
        private final CustomStatus customStatus;

        public Settings(String locale, boolean showCurrentGame, List<Long> restrictedGuilds, boolean defaultGuildsRestricted, boolean inlineAttachmentMedia, boolean inlineEmbedMedia, boolean gifAutoPlay, boolean renderEmbeds, boolean renderReactions, boolean animateEmoji, boolean enableTtsCommand, boolean messageDisplayCompact, boolean convertEmoticons, ContentFilter explicitContentFilter, boolean disableGamesTab, Theme theme, boolean developerMode, List<Long> guildPositions, boolean detectPlatformAccounts, Status status, int afkTimeout, int timezoneOffset, boolean streamNotificationsEnabled, boolean allowAccessibilityDetection, boolean contactSyncEnabled, boolean nativePhoneIntegrationEnabled, int animateStickers, int friendDiscoveryFlags, List<ServerFolder> guildFolders, CustomStatus customStatus) {
            this.locale = locale;
            this.showCurrentGame = showCurrentGame;
            this.restrictedGuilds = restrictedGuilds;
            this.defaultGuildsRestricted = defaultGuildsRestricted;
            this.inlineAttachmentMedia = inlineAttachmentMedia;
            this.inlineEmbedMedia = inlineEmbedMedia;
            this.gifAutoPlay = gifAutoPlay;
            this.renderEmbeds = renderEmbeds;
            this.renderReactions = renderReactions;
            this.animateEmoji = animateEmoji;
            this.enableTtsCommand = enableTtsCommand;
            this.messageDisplayCompact = messageDisplayCompact;
            this.convertEmoticons = convertEmoticons;
            this.explicitContentFilter = explicitContentFilter;
            this.disableGamesTab = disableGamesTab;
            this.theme = theme;
            this.developerMode = developerMode;
            this.guildPositions = guildPositions;
            this.detectPlatformAccounts = detectPlatformAccounts;
            this.status = status;
            this.afkTimeout = afkTimeout;
            this.timezoneOffset = timezoneOffset;
            this.streamNotificationsEnabled = streamNotificationsEnabled;
            this.allowAccessibilityDetection = allowAccessibilityDetection;
            this.contactSyncEnabled = contactSyncEnabled;
            this.nativePhoneIntegrationEnabled = nativePhoneIntegrationEnabled;
            this.animateStickers = animateStickers;
            this.friendDiscoveryFlags = friendDiscoveryFlags;
            this.guildFolders = guildFolders;
            this.customStatus = customStatus;
        }

        public String getLocale() {
            return locale;
        }

        public boolean isShowCurrentGame() {
            return showCurrentGame;
        }

        public List<Long> getRestrictedGuilds() {
            return restrictedGuilds;
        }

        public boolean isDefaultGuildsRestricted() {
            return defaultGuildsRestricted;
        }

        public boolean isInlineAttachmentMedia() {
            return inlineAttachmentMedia;
        }

        public boolean isInlineEmbedMedia() {
            return inlineEmbedMedia;
        }

        public boolean isGifAutoPlay() {
            return gifAutoPlay;
        }

        public boolean isRenderEmbeds() {
            return renderEmbeds;
        }

        public boolean isRenderReactions() {
            return renderReactions;
        }

        public boolean isAnimateEmoji() {
            return animateEmoji;
        }

        public boolean isEnableTtsCommand() {
            return enableTtsCommand;
        }

        public boolean isMessageDisplayCompact() {
            return messageDisplayCompact;
        }

        public boolean isConvertEmoticons() {
            return convertEmoticons;
        }

        public ContentFilter getExplicitContentFilter() {
            return explicitContentFilter;
        }

        public boolean isDisableGamesTab() {
            return disableGamesTab;
        }

        public Theme getTheme() {
            return theme;
        }

        public boolean isDeveloperMode() {
            return developerMode;
        }

        public List<Long> getGuildPositions() {
            return guildPositions;
        }

        public boolean isDetectPlatformAccounts() {
            return detectPlatformAccounts;
        }

        public Status getStatus() {
            return status;
        }

        public int getAfkTimeout() {
            return afkTimeout;
        }

        public int getTimezoneOffset() {
            return timezoneOffset;
        }

        public boolean isStreamNotificationsEnabled() {
            return streamNotificationsEnabled;
        }

        public boolean isAllowAccessibilityDetection() {
            return allowAccessibilityDetection;
        }

        public boolean isContactSyncEnabled() {
            return contactSyncEnabled;
        }

        public boolean isNativePhoneIntegrationEnabled() {
            return nativePhoneIntegrationEnabled;
        }

        public int getAnimateStickers() {
            return animateStickers;
        }

        public int getFriendDiscoveryFlags() {
            return friendDiscoveryFlags;
        }

        public List<ServerFolder> getGuildFolders() {
            return guildFolders;
        }

        public CustomStatus getCustomStatus() {
            return customStatus;
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "locale='" + locale + '\'' +
                    ", showCurrentGame=" + showCurrentGame +
                    ", restrictedGuilds=" + restrictedGuilds +
                    ", defaultGuildsRestricted=" + defaultGuildsRestricted +
                    ", inlineAttachmentMedia=" + inlineAttachmentMedia +
                    ", inlineEmbedMedia=" + inlineEmbedMedia +
                    ", gifAutoPlay=" + gifAutoPlay +
                    ", renderEmbeds=" + renderEmbeds +
                    ", renderReactions=" + renderReactions +
                    ", animateEmoji=" + animateEmoji +
                    ", enableTtsCommand=" + enableTtsCommand +
                    ", messageDisplayCompact=" + messageDisplayCompact +
                    ", convertEmoticons=" + convertEmoticons +
                    ", explicitContentFilter=" + explicitContentFilter +
                    ", disableGamesTab=" + disableGamesTab +
                    ", theme=" + theme +
                    ", developerMode=" + developerMode +
                    ", guildPositions=" + guildPositions +
                    ", detectPlatformAccounts=" + detectPlatformAccounts +
                    ", status=" + status +
                    ", afkTimeout=" + afkTimeout +
                    ", timezoneOffset=" + timezoneOffset +
                    ", streamNotificationsEnabled=" + streamNotificationsEnabled +
                    ", allowAccessibilityDetection=" + allowAccessibilityDetection +
                    ", contactSyncEnabled=" + contactSyncEnabled +
                    ", nativePhoneIntegrationEnabled=" + nativePhoneIntegrationEnabled +
                    ", animateStickers=" + animateStickers +
                    ", friendDiscoveryFlags=" + friendDiscoveryFlags +
                    ", guildFolders=" + guildFolders +
                    ", customStatus=" + customStatus +
                    '}';
        }
    }
}
