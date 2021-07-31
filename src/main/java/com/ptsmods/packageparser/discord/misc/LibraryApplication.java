package com.ptsmods.packageparser.discord.misc;

import com.ptsmods.packageparser.Pair;

import java.util.Collections;
import java.util.List;

public class LibraryApplication {
    private final long id;
    private final String name;
    private final String icon; // TODO idk how to parse this. I'd assume it's part of a url (baseUrl + "/" + id + "/" + icon + ".png"), but idk what the baseUrl is.
    private final String description;
    private final String summary;
    private final String splash; // TODO same as icon
    private final String slug;
    private final List<Pair<String, String>> publishers, developers;

    public LibraryApplication(long id, String name, String icon, String description, String summary, String splash, String slug, List<Pair<String, String>> publishers, List<Pair<String, String>> developers) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.summary = summary;
        this.splash = splash;
        this.slug = slug;
        this.publishers = Collections.unmodifiableList(publishers);
        this.developers = Collections.unmodifiableList(developers);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getSummary() {
        return summary;
    }

    public String getSplash() {
        return splash;
    }

    public String getSlug() {
        return slug;
    }

    public List<Pair<String, String>> getPublishers() {
        return publishers;
    }

    public List<Pair<String, String>> getDevelopers() {
        return developers;
    }
}
