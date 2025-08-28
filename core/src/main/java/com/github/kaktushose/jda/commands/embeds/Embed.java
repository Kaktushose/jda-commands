package com.github.kaktushose.jda.commands.embeds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.i18n.I18n;
import com.github.kaktushose.jda.commands.i18n.Localizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/// Subclass of [EmbedBuilder] that supports placeholders and easier manipulation of fields.
public class Embed extends EmbedBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;
    private final Map<String, Object> placeholders;
    private final I18n i18n;
    private Locale locale;

    /// Constructs a new [Embed].
    ///
    /// @param embedBuilder the underlying [EmbedBuilder] to use
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global placeholders as defined in [Embeds]
    public Embed(EmbedBuilder embedBuilder, String name, Map<String, Object> placeholders, I18n i18n) {
        super(embedBuilder);
        this.name = name;
        this.placeholders = new HashMap<>(placeholders);
        this.i18n = i18n;
        locale = Locale.ENGLISH;
    }

    /// Constructs a new [Embed].
    ///
    /// @param object       the [DataObject] to construct the underlying [EmbedBuilder] from
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global placeholders as defined in [Embeds]
    public Embed(DataObject object, String name, Map<String, Object> placeholders, I18n i18n) {
        this(EmbedBuilder.fromData(object), name, placeholders, i18n);
    }

    /// Sets the [Locale] this [Embed] will be localized with.
    ///
    /// @param locale the [Locale] to use for localization
    /// @return this instance for fluent interface
    /// @see I18n
    public Embed locale(Locale locale) {
        this.locale = locale;
        return this;
    }


    /// Gets the name of this embed. This isn't a field that get displayed but only the name this embed is referenced
    /// by in the [EmbedDataSource].
    ///
    /// @return the name of this embed
    public String name() {
        return name;
    }


    /// Sets the Title of the embed.
    ///
    /// @param title the title of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setTitle(String)
    public Embed title(@Nullable String title) {
        setTitle(title);
        return this;
    }

    /// Sets the Title of the embed.
    ///
    /// @param title the title of the embed
    /// @param url   Makes the title into a hyperlink pointed at this url.
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setTitle(String, String)
    public Embed title(@Nullable String title, @Nullable String url) {
        setTitle(title, url);
        return this;
    }

    /// Sets the Description of the embed.
    ///
    /// @param description the description of the embed, `null` to reset
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setDescription(CharSequence)
    public Embed description(@Nullable CharSequence description) {
        setDescription(description);
        return this;
    }

    /// Sets the Color of the embed.
    ///
    /// @param color The raw rgb value, or [Role#DEFAULT_COLOR_RAW] to use no color
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setColor(int)
    public Embed color(int color) {
        setColor(color);
        return this;
    }

    /// Sets the Color of the embed.
    ///
    /// @param color The [Color] of the embed or `null` to use no color
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setColor(Color)
    public Embed color(@Nullable Color color) {
        setColor(color);
        return this;
    }

    /// Sets the Timestamp of the embed.
    ///
    /// @param accessor the temporal accessor of the timestamp
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setTimestamp(TemporalAccessor)
    public Embed timestamp(@Nullable TemporalAccessor accessor) {
        setTimestamp(accessor);
        return this;
    }

    /// Sets the Footer of the embed.
    ///
    /// @param footer the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setFooter(String)
    public Embed footer(@Nullable String footer) {
        setFooter(footer);
        return this;
    }

    /// Sets the Footer of the embed.
    ///
    /// @param footer  the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
    /// @param iconUrl the url of the icon for the footer
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setFooter(String, String)
    public Embed footer(@Nullable String footer, @Nullable String iconUrl) {
        setFooter(footer, iconUrl);
        return this;
    }

    /// Sets the Thumbnail of the embed.
    ///
    /// @param url the url of the thumbnail of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setThumbnail(String)
    public Embed thumbnail(@Nullable String url) {
        setThumbnail(url);
        return this;
    }

    /// Sets the Image of the embed.
    ///
    /// @param url the url of the image of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setImage(String)
    public Embed image(@Nullable String url) {
        setImage(url);
        return this;
    }

    /// Sets the Author of the embed.
    ///
    /// @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setAuthor(String)
    public Embed author(@Nullable String name) {
        setAuthor(name);
        return this;
    }

    /// Sets the Author of the embed.
    ///
    /// @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
    /// @param url  the url of the author of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setAuthor(String, String)
    public Embed author(@Nullable String name, @Nullable String url) {
        setAuthor(name, url);
        return this;
    }

    /// Sets the Author of the embed.
    ///
    /// @param name    the name of the author of the embed. If this is not set, the author will not appear in the embed
    /// @param url     the url of the author of the embed
    /// @param iconUrl the url of the icon of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setAuthor(String, String, String)
    public Embed author(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        setAuthor(name, url, iconUrl);
        return this;
    }

    /// Used to modify the fields of this embed.
    ///
    /// @return this instance for fluent interface
    public Fields fields() {
        return new Fields() {
            @Override
            public Fields removeIf(Predicate<MessageEmbed.Field> filter) {
                getFields().removeIf(filter);
                return this;
            }
        };
    }

    /// Adds a Field to the embed that isn't inlined.
    ///
    /// @param name  the name of the Field, displayed in bold above the value.
    /// @param value the contents of the field.
    /// @return this instance for fluent interface
    public Embed addField(String name, String value) {
        addField(name, value, false);
        return this;
    }

    /// Adds all the provided placeholders to this embed instance. The values will be replaced when [#build()] is called.
    ///
    /// Existing entries with the same keys will be overwritten.
    ///
    /// Internally this uses the localization system, thus placeholders are limited by the used [Localizer] implementation
    ///
    /// @param placeholders a map of placeholder names to their corresponding values
    /// @return this instance for fluent interface
    public Embed placeholders(Map<String, Object> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    /// Adds all the provided [`placeholders`][I18n.Entry] to this embed instance. The values will be replaced when [#build()] is called.
    ///
    /// Existing entries with the same keys will be overwritten.
    ///
    /// Internally this uses the localization system, thus placeholders are limited by the used [Localizer] implementation
    ///
    /// @param placeholders the [`entries`][I18n.Entry] to add
    /// @return this instance for fluent interface
    public Embed placeholders(I18n.Entry... placeholders) {
        this.placeholders.putAll(Arrays.stream(placeholders).collect(Collectors.toMap(I18n.Entry::name, I18n.Entry::value)));
        return this;
    }

    /// Returns a [MessageEmbed] just like [EmbedBuilder#build()], but will also localize this embed based on the
    /// [#locale(Locale)] and [`placeholders`][#placeholders(I18n.Entry...)] provided.
    ///
    /// @return the built, sendable [MessageEmbed]
    @Override
    public MessageEmbed build() {
        String json = super.build().toData().toString();
        try {
            JsonNode node = localize(mapper.readTree(json));
            return EmbedBuilder.fromData(DataObject.fromJson(node.toString())).build();
        } catch (JsonProcessingException e) {
            throw new InternalException("localization-json-error", e);
        }
    }

    private JsonNode localize(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                JsonNode child = entry.getValue();
                JsonNode newChild = localize(child);
                if (newChild.isTextual()) {
                    objectNode.put(entry.getKey(), i18n.localize(locale, newChild.asText(), placeholders));
                } else {
                    objectNode.set(entry.getKey(), newChild);
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode child = arrayNode.get(i);
                arrayNode.set(i, localize(child));
            }
        }
        return node;
    }

    /// Transforms this embed into [MessageCreateData].
    ///
    /// @return the transformed [MessageCreateData]
    public MessageCreateData toMessageCreateData() {
        return MessageCreateData.fromEmbeds(build());
    }

    /// Transforms this embed into [MessageEditData].
    ///
    /// @return the transformed [MessageEditData]
    public MessageEditData toMessageEditData() {
        return MessageEditData.fromEmbeds(build());
    }

    /// Methods for manipulating the fields of an [Embed].
    public interface Fields {

        /// Removes all fields of this embed based on the given [Predicate].
        ///
        /// @param filter the [Predicate] to test the fields with
        /// @return this instance for fluent interface
        Fields removeIf(Predicate<MessageEmbed.Field> filter);

        /// Removes all fields with the given name of this embed based on the given [Predicate].
        ///
        /// @param name   the name of the fields to test
        /// @param filter the [Predicate] to test the fields with
        /// @return this instance for fluent interface
        default Fields removeIf(String name, Predicate<MessageEmbed.Field> filter) {
            return removeIf(filter.and(field -> name.equals(field.getName())));
        }

        /// Removes all fields with the given value.
        ///
        /// @param value the value of a field that should be removed
        /// @return this instance for fluent interface
        default Fields remove(String value) {
            return removeIf(field -> value.equals(field.getValue()));
        }

        /// Removes all fields with the given name.
        ///
        /// @param name the name of a field that should be removed
        /// @return this instance for fluent interface
        default Fields removeByName(String name) {
            return removeIf(name, _ -> true);
        }

        /// Removes all fields with the given name **and** value.
        ///
        /// @param name  the name of the field that should be removed
        /// @param value the value of the field that should be removed
        /// @return this instance for fluent interface
        default Fields remove(String name, String value) {
            return removeIf(name, field -> value.equals(field.getValue()));
        }
    }
}
