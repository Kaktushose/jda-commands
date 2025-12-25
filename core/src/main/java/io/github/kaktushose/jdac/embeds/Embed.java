package io.github.kaktushose.jdac.embeds;

import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.i18n.EmbedResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;
import static net.dv8tion.jda.api.EmbedBuilder.ZERO_WIDTH_SPACE;

/// Builder for [MessageEmbed] that supports placeholders, localization and easier manipulation of [Field]s. Can also be
/// loaded via [EmbedDataSource].
public class Embed {

    private final String name;
    private final Map<String, @Nullable Object> placeholders;
    private final EmbedResolver embedLocalizer;
    private DataObject data;
    private Locale locale;

    private Embed(DataObject object, String name, Map<String, @Nullable Object> placeholders, MessageResolver messageResolver) {
        this.name = name;
        this.placeholders = new HashMap<>(placeholders);
        this.embedLocalizer = new EmbedResolver(messageResolver);
        locale = Locale.ENGLISH;
        this.data = object;
    }

    /// Constructs a new [Embed].
    ///
    /// @param embedBuilder the [EmbedBuilder] to construct the [Embed] from
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global placeholders as defined in [Embeds]
    public static Embed of(EmbedBuilder embedBuilder, String name, Map<String, @Nullable Object> placeholders, MessageResolver messageResolver) {
        return of(embedBuilder.build().toData(), name, placeholders, messageResolver);
    }

    /// Constructs a new [Embed].
    ///
    /// @param object       the [DataObject] to construct the [Embed] from
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global placeholders as defined in [Embeds]
    public static Embed of(DataObject object, String name, Map<String, @Nullable Object> placeholders, MessageResolver messageResolver) {
        return new Embed(object, name, placeholders, messageResolver);
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
        data.put("title", title);
        return this;
    }

    /// Sets the URL of the embed.
    ///
    /// The Discord client mostly only uses this property in combination with the [title][#title(String)] for a clickable Hyperlink.
    ///
    /// If multiple embeds in a message use the same URL, the Discord client will merge them into a single embed and aggregate images into a gallery view.
    ///
    /// @return the builder after the URL has been set
    /// @see EmbedBuilder#setUrl(String)
    /// @see #title(String) (String, String)
    public Embed url(@Nullable String url) {
        urlCheck(url);
        data.put("url", url);
        return this;
    }

    /// Sets the Title of the embed.
    ///
    /// @param title the title of the embed
    /// @param url   Makes the title into a hyperlink pointed at this url.
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setTitle(String, String)
    public Embed title(@Nullable String title, @Nullable String url) {
        return title(title).url(url);
    }

    /// Sets the Description of the embed.
    ///
    /// @param description the description of the embed, `null` to reset
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setDescription(CharSequence)
    public Embed description(@Nullable CharSequence description) {
        data.put("description", description);
        return this;
    }

    /// Sets the Color of the embed.
    ///
    /// @param color The raw rgb value, or [Role#DEFAULT_COLOR_RAW] to use no color
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setColor(int)
    public Embed color(int color) {
        data.put("color", color);
        return this;
    }

    /// Sets the Color of the embed.
    ///
    /// @param color The [Color] of the embed or `null` to use no color
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setColor(Color)
    public Embed color(@Nullable Color color) {
        data.put("color", color == null ? null : color.getRGB());
        return this;
    }

    /// Sets the Timestamp of the embed.
    ///
    /// @param accessor the temporal accessor of the timestamp
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setTimestamp(TemporalAccessor)
    public Embed timestamp(@Nullable TemporalAccessor accessor) {
        data.put("timestamp", accessor == null ? null : accessor.toString());
        return this;
    }

    /// Sets the Footer of the embed.
    ///
    /// @param footer the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setFooter(String)
    public Embed footer(@Nullable String footer) {
        return footer(footer, null);
    }

    /// Sets the Footer of the embed.
    ///
    /// @param footer  the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
    /// @param iconUrl the url of the icon for the footer
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setFooter(String, String)
    public Embed footer(@Nullable String footer, @Nullable String iconUrl) {
        if (iconUrl == null && footer == null) {
            return this;
        }
        urlCheck(iconUrl);
        data.put("footer", DataObject.empty().put("text", footer).put("icon_url", iconUrl));
        return this;
    }

    /// Sets the Thumbnail of the embed.
    ///
    /// @param url the url of the thumbnail of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setThumbnail(String)
    public Embed thumbnail(@Nullable String url) {
        if (url == null) {
            return this;
        }
        urlCheck(url);
        data.put("thumbnail", DataObject.empty().put("url", url));
        return this;
    }

    /// Sets the Image of the embed.
    ///
    /// @param url the url of the image of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setImage(String)
    public Embed image(@Nullable String url) {
        if (url == null) {
            return this;
        }
        urlCheck(url);
        data.put("image", DataObject.empty().put("url", url));
        return this;
    }

    /// Sets the Author of the embed.
    ///
    /// @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setAuthor(String)
    public Embed author(@Nullable String name) {
        return author(name, null, null);
    }

    /// Sets the Author of the embed.
    ///
    /// @param name the name of the author of the embed. If this is not set, the author will not appear in the embed
    /// @param url  the url of the author of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setAuthor(String, String)
    public Embed author(@Nullable String name, @Nullable String url) {
        return author(name, url, null);
    }

    /// Sets the Author of the embed.
    ///
    /// @param name    the name of the author of the embed. If this is not set, the author will not appear in the embed
    /// @param url     the url of the author of the embed
    /// @param iconUrl the url of the icon of the embed
    /// @return this instance for fluent interface
    /// @see EmbedBuilder#setAuthor(String, String, String)
    public Embed author(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        if (name == null && url == null && iconUrl == null) {
            return this;
        }
        urlCheck(url);
        urlCheck(iconUrl);
        data.put("author", DataObject.empty().put("name", name).put("url", url).put("icon_url", iconUrl));
        return this;
    }

    /// Used to modify the fields of this embed.
    ///
    /// @return this instance for fluent interface
    public Fields fields() {
        return new Fields() {
            @Override
            public Fields add(String name, String value, boolean inline) {
                Checks.notNull(name, "Name");
                Checks.notNull(value, "Value");
                DataArray array;
                if (data.hasKey("fields")) {
                    array = data.getArray("fields");
                } else {
                    array = DataArray.empty();
                    data.put("fields", array);
                }
                array.add(DataObject.empty().put("name", name).put("value", value).put("inline", inline));
                return this;
            }

            @Override
            public Fields removeIf(Predicate<Field> filter) {
                var fields = new ArrayList<>(getFields());
                fields.removeIf(filter);
                // this wrapping is very important, otherwise the DataObject keeps the type information (Field) and
                // subsequent calls break
                data.put("fields", DataArray.fromJson(DataArray.fromCollection(fields).toString()));
                return this;
            }

            @Override
            public Fields replace(Predicate<Field> filter, Field field) {
                if (!data.hasKey("fields")) {
                    return this;
                }
                data.getArray("fields")
                        .stream(DataArray::getObject)
                        .filter(it -> filter.test(getField(it)))
                        .forEach(it -> it.put("name", field.getName())
                                .put("value", field.getValue())
                                .put("inline", field.isInline())
                        );
                return this;
            }
        };
    }

    private List<Field> getFields() {
        if (!data.hasKey("fields")) {
            return List.of();
        }
        return data.getArray("fields").stream(DataArray::getObject).map(this::getField).toList();
    }

    private Field getField(DataObject object) {
        return new Field(
                object.getString("name", ZERO_WIDTH_SPACE),
                object.getString("value", ZERO_WIDTH_SPACE),
                object.getBoolean("inline", false)
        );
    }

    /// Resets this builder to default state.
    ///
    /// **All parts will be either empty or null after this method has returned.**
    ///
    /// @return The current EmbedBuilder with default values
    public Embed clear() {
        data = DataObject.empty();
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
    public Embed placeholders(Map<String, @Nullable Object> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    /// Adds all the provided [`placeholders`][Entry] to this embed instance. The values will be replaced when [#build()] is called.
    ///
    /// Existing entries with the same keys will be overwritten.
    ///
    /// Internally this uses the localization system, thus placeholders are limited by the used [Localizer] implementation
    ///
    /// @param placeholders the [`entries`][Entry] to add
    /// @return this instance for fluent interface
    public Embed placeholders(Entry... placeholders) {
        this.placeholders.putAll(Arrays.stream(placeholders)
                .collect(HashMap::new, (m,e)->m.put(e.name(), e.value()), HashMap::putAll));
        return this;
    }

    /// Returns a [MessageEmbed] just like [EmbedBuilder#build()], but will also localize this embed based on the
    /// [#locale(Locale)] and [`placeholders`][#placeholders(Entry...)] provided.
    ///
    /// @return the built, sendable [MessageEmbed]
    public MessageEmbed build() {
        return embedLocalizer.resolve(EmbedBuilder.fromData(data).build(), locale, placeholders);
    }

    private void urlCheck(@Nullable String url) {
        if (url != null) {
            Checks.notLonger(url, MessageEmbed.URL_MAX_LENGTH, "URL");
            Checks.check(URL_PATTERN.matcher(url).matches(), "URL must be a valid http(s) or attachment url.");
        }
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

        /// Adds a Field to the embed that isn't inlined.
        ///
        /// @param name  the name of the Field, displayed in bold above the value.
        /// @param value the contents of the field.
        /// @return this instance for fluent interface
        default Fields add(String name, String value) {
            return add(name, value, false);
        }

        /// Copies the provided Field into a new Field for this builder.
        ///
        /// For additional documentation, see [#add(String,String,boolean)]
        ///
        /// @param field the field object to add
        /// @return the builder after the field has been added
        default Fields add(@Nullable Field field) {
            return field == null ? this : add(field.getName(), field.getValue(), field.isInline());
        }

        /// Adds a blank (empty) Field to the embed.
        ///
        /// [Example of Inline](https://raw.githubusercontent.com/discord-jda/JDA/assets/assets/docs/embeds/07-addField.png)
        /// [Example of Non-inline](https://raw.githubusercontent.com/discord-jda/JDA/assets/assets/docs/embeds/08-addField.png)
        ///
        /// @param inline whether this field should display inline
        /// @return the builder after the field has been added
        default Fields add(boolean inline) {
            return add(new Field(ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, inline));
        }

        /// Adds a Field to the embed.
        ///
        /// Note: If a blank string is provided to either `name` or `value`, the blank string is replaced
        /// with [EmbedBuilder#ZERO_WIDTH_SPACE].
        ///
        /// [Example of Inline](https://raw.githubusercontent.com/discord-jda/JDA/assets/assets/docs/embeds/07-addField.png)
        /// [Example of Non-inline](https://raw.githubusercontent.com/discord-jda/JDA/assets/assets/docs/embeds/08-addField.png)
        ///
        /// @param name   the name of the Field, displayed in bold above the `value`.
        /// @param value  the contents of the field.
        /// @param inline whether this field should display inline.
        /// @return the builder after the field has been added
        /// @throws java.lang.IllegalArgumentException - If `null` is provided
        ///                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              - If the character limit of {@value MessageEmbed#TITLE_MAX_LENGTH} for `name` is exceeded.
        ///                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              - If the character limit of {@value MessageEmbed#VALUE_MAX_LENGTH} for `value` is exceeded.
        ///
        Fields add(String name, String value, boolean inline);

        /// Removes all fields with the given name of this embed based on the given [Predicate].
        ///
        /// @param name   the name of the fields to test
        /// @param filter the [Predicate] to test the fields with
        /// @return this instance for fluent interface
        default Fields removeIf(String name, Predicate<Field> filter) {
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

        /// Clears all fields from the embed.
        ///
        /// @return this instance for fluent interface
        default Fields clear() {
            return removeIf(_ -> true);
        }

        /// Removes all fields of this embed based on the given [Predicate].
        ///
        /// @param filter the [Predicate] to test the fields with
        /// @return this instance for fluent interface
        Fields removeIf(Predicate<Field> filter);

        /// Replaces all fields of this embed with the given [Field] based on the given name.
        ///
        /// @param name  the name of a field that should be replaced
        /// @param field the new [Field] to replace the old value with
        /// @return this instance for fluent interface
        default Fields replace(String name, Field field) {
            return replace(it -> name.equals(it.getName()), field);
        }

        /// Replaces all fields of this embed with the given [Field] based on the given value.
        ///
        /// @param value the value of a field that should be replaced
        /// @param field the new [Field] to replace the old value with
        /// @return this instance for fluent interface
        default Fields replaceByValue(String value, Field field) {
            return replace(it -> value.equals(it.getValue()), field);
        }

        /// Replaces all fields of this embed based on the [Predicate] with the given [Field].
        ///
        /// @param filter the [Predicate] to test the fields with
        /// @param field  the new [Field] to replace the old values with
        /// @return this instance for fluent interface
        Fields replace(Predicate<Field> filter, Field field);

    }
}
