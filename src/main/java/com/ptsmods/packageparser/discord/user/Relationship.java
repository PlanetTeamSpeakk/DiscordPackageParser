package com.ptsmods.packageparser.discord.user;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Relationship {
    private final long id;
    private final Type type;
    private final String nickname;
    private final String username;
    private final Supplier<Image> avatarSupplier;
    private Image avatar;
    private final int discriminator;
    private final int publicFlags;

    public Relationship(long id, Type type, String nickname, String username, String avatarId, int discriminator, int publicFlags) {
        this.id = id;
        this.type = type;
        this.nickname = nickname;
        this.username = username;
        this.avatarSupplier = () -> {
            try {
                return ImageIO.read(new URL("https://cdn.discordapp.com/avatars/" + id + "/" + avatarId + ".png"));
            } catch (IOException e) {
                System.err.println("Could not get avatar for user " + username + "#" + discriminator);
                return new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            }
        };
        this.discriminator = discriminator;
        this.publicFlags = publicFlags;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getTag() {
        return getUsername() + '#' + String.format("%04d", getDiscriminator());
    }

    public Image getAvatar() {
        return avatar == null ? avatar = avatarSupplier.get() : avatar;
    }

    public CompletableFuture<Image> getAvatarAsync() {
        return CompletableFuture.supplyAsync(this::getAvatar);
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public int getPublicFlags() {
        return publicFlags;
    }

    public enum Type {
        FRIEND, BLOCKED, PENDING_INCOMING, PENDING_OUTGOING
    }
}
