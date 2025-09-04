package com.github.kaktushose.jda.commands.embeds.internal;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

/// This is a subclass of [DataObject] that manipulates resolved values for certain keys. Namely, these keys are
/// - `color`
/// - `url`
/// - `icon_url`
///
/// The keys `url` and `icon_url` will also work for children named:
/// - `thumbnail`
/// - `author`
/// - `footer`
/// - `image`
///
/// The underlying data (stored as `Map<String, Object>`) will not be manipulated, only the returned value is affected.
/// If the value is valid, the original value will be returned. If the value is invalid, will return the provided default
/// value or if `null`, a dummy value.
@ApiStatus.Internal
public class LocalizableDataObject extends DataObject {

    private static final String validUrl = "https://cdn.discordapp.com/embed/avatars/0.png";
    private static final List<String> fields = List.of("thumbnail", "author", "footer", "image");

    public LocalizableDataObject(Map<String, Object> data) {
        super(data);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        if (!"color".equals(key)) {
            return super.getInt(key, defaultValue);
        }
        String raw = getString("color", null);
        if (raw == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException _) {
            return defaultValue;
        }
    }

    @Override
    public Optional<DataObject> optObject(String key) {
        if (!fields.contains(key)) {
            return super.optObject(key);
        }
        Map<String, Object> child = getInternal(key);
        return child == null ? Optional.empty() : Optional.of(new LocalizableDataObject(child));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Map<String, Object> getInternal(String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        return (Map<String, Object>) value;
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defaultValue) {
        String value = super.getString(key, defaultValue);
        return switch (key) {
            case "url", "icon_url" -> isValidUrl(value) ? value : validUrl;
            case "timestamp" -> isValidTimestamp(value) ? value : Instant.now().toString();
            default -> value;
        };
    }

    private boolean isValidUrl(@Nullable String url) {
        if (url == null) {
            return false;
        }
        return Helpers.codePointLength(url) <= MessageEmbed.URL_MAX_LENGTH && URL_PATTERN.matcher(url).matches();
    }

    private boolean isValidTimestamp(@Nullable String timestamp) {
        if (timestamp == null) {
            return false;
        }
        try {
            OffsetDateTime.parse(timestamp);
            return true;
        } catch (DateTimeParseException _) {
            return false;
        }
    }
}
