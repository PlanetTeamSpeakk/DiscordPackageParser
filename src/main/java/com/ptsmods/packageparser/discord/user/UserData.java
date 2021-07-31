package com.ptsmods.packageparser.discord.user;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.ptsmods.packageparser.PackageParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Class used to parse json data from api.
public class UserData {
    private static final Map<Long, UserData> dataCache = new ConcurrentHashMap<>();
    private static final Executor executor = Executors.newFixedThreadPool(1); // To make sure we don't get rate-limited.
    private static final File cacheFile = PackageParser.resolvePath("userCache.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Long, CompletableFuture<UserData>> ongoingFutures = new HashMap<>();
    public String id;
    public String username;
    public String avatar;
    public String discriminator;
    public int public_flags;
    public String message; // for errors
    public int code; // for errors
    private transient BufferedImage avatarImg = null; // Transient so Gson ignores it, causes illegal reflective operations otherwise.

    static {
        if (cacheFile.exists())
            try {
                dataCache.putAll(gson.fromJson(String.join("\n", Files.readAllLines(cacheFile.toPath())), new TypeToken<Map<Long, UserData>>(){}.getType()));
            } catch (Exception e) {
                System.err.println("Could not read userdata cache.");
                e.printStackTrace();
            }
    }

    @Override
    public String toString() {
        return "UserData{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", discriminator='" + discriminator + '\'' +
                ", public_flags=" + public_flags +
                ", message='" + message + '\'' +
                ", code=" + code +
                '}';
    }

    public CompletableFuture<BufferedImage> getAvatarImg() {
        return avatarImg == null ? CompletableFuture.supplyAsync(() -> {
            try {
                return avatarImg = ImageIO.read(new URL(avatar == null ? "https://cdn.discordapp.com/embed/avatars/" + (Integer.parseInt(discriminator) % 5) + ".png" : "https://cdn.discordapp.com/avatars/" + id + "/" + avatar));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }) : CompletableFuture.completedFuture(avatarImg);
    }

    public static synchronized CompletableFuture<UserData> getUserData(long id) {
        return dataCache.containsKey(id) ? CompletableFuture.completedFuture(dataCache.get(id).message == null ? dataCache.get(id) : null) : ongoingFutures.computeIfAbsent(id, id0 -> CompletableFuture.supplyAsync(() -> {
            try {
                // If you want to use this API yourself, I'd rather you don't, but if you do, don't include the 'packageparser' parameter.
                // That way I can monitor a bit how many people access this API directly.
                URLConnection con = new URL("https://api.ptsmods.com/userLookup.php?packageparser&userid=" + id).openConnection();
                UserData data = new Gson().fromJson(new BufferedReader(new InputStreamReader(con.getInputStream())), UserData.class);
                dataCache.put(id, data.message == null ? data : null);
                Thread.sleep(500); // Preventing rate-limiting
                if (data.message != null) return null;
                else {
                    CompletableFuture.runAsync(() -> {
                        synchronized (dataCache) {
                            Map<Long, UserData> dataCache = new HashMap<>(UserData.dataCache);
                            try (PrintWriter writer = new PrintWriter(cacheFile, "UTF-8")) {
                                writer.print(gson.toJson(dataCache));
                                writer.flush();
                            } catch (IOException e) {
                                System.err.println("Could not save userdata cache file.");
                                e.printStackTrace();
                            }
                        }
                    });
                    return data;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }, executor));
    }
}
