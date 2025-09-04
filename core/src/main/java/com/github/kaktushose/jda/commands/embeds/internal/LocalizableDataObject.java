package com.github.kaktushose.jda.commands.embeds.internal;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

public class LocalizableDataObject extends DataObject {

    private static final String validUrl = "https://cdn.discordapp.com/embed/avatars/0.png";
    private final String name;
    @Nullable
    private String tempColor;
    @Nullable
    private String tempTimestamp;
    private final Map<String, String> tempUrls;
    private static final List<String> fields = List.of("thumbnail", "author", "footer", "image");

    public LocalizableDataObject(DataObject object) {
        this(object.toMap(), "root");
    }


    public LocalizableDataObject(Map<String, Object> data, String name) {
        super(data);
        tempUrls = new HashMap<>();
        this.name = name;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        if (!"color".equals(key)) {
            return super.getInt(key, defaultValue);
        }
        String raw = getString("color");
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException _) {
            tempColor = raw;
            return defaultValue;
        }
    }

    @Override
    public Optional<DataObject> optObject(String key) {
        if (!fields.contains(key)) {
            return super.optObject(key);
        }
        Map<String, Object> child = getObj(key);
        return child == null ? Optional.empty() : Optional.of(new LocalizableDataObject(child, key));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Map<String, Object> getObj(String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        return (Map<String, Object>) value;
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defaultValue) {
        if (!("url".equals(key) || "iconUrl".equals(key) || "timestamp".equals(key))) {
            return super.getString(key, defaultValue);
        }
        if ("timestamp".equals(key)) {
            String timestamp = super.getString("timestemp", null);
            try {
                return timestamp;
            } catch (DateTimeParseException _) {
                tempTimestamp = timestamp;
                return null;
            }
        }
        String url = checkUrl(super.getString(key, defaultValue), "%s%s".formatted(name, key));
        return url == null ? url : validUrl;
    }

    public Optional<String> getTempColor() {
        return Optional.ofNullable(tempColor);
    }

    public Optional<String> getTempUrl(String key) {
        return Optional.ofNullable(tempUrls.get(key));
    }

    @Nullable
    private String checkUrl(@Nullable String url, String name) {
        if (isInvalidUrl(url)) {
            tempUrls.put(name, url);
            return null;
        }
        return url;
    }

    private boolean isInvalidUrl(@Nullable String url) {
        if (url == null) {
            return false;
        }
        return !(Helpers.codePointLength(url) <= MessageEmbed.URL_MAX_LENGTH && URL_PATTERN.matcher(url).matches());
    }

    public Map<String, String> getTempUrls() {
        return tempUrls;
    }
}
