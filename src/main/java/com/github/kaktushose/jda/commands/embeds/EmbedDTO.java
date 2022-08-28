package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a DTO to serialize and deserialize JDA's embed objects to json. Checkout the discord docs to get
 * information about what each field does exactly.
 *
 * <p>The json object can contain {@code {placeholders}} which can then be injected with values at runtime by calling
 * {@link #injectValue(String, Object)} or {@link #injectValues(Map)}. Alternatively you can use
 * {@link #injectFields(Object...)} to inject the fields of objects.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object">Discord Embed Documentation</a>
 * @see EmbedCache
 * @since 1.1.0
 */
public class EmbedDTO implements Serializable {

    private static final long serialVersionUID = 0L;
    private String title;
    private String description;
    private String url;
    private String color;
    private String timestamp;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private Author author;
    private Field[] fields;

    public EmbedDTO() {
    }

    public EmbedDTO(String title,
                    String description,
                    String url,
                    String color,
                    String timestamp,
                    Footer footer,
                    Thumbnail thumbnail,
                    Image image,
                    Author author,
                    Field[] fields) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.color = color;
        this.timestamp = timestamp;
        this.footer = footer;
        this.thumbnail = thumbnail;
        this.image = image;
        this.author = author;
        this.fields = fields;
    }

    public EmbedDTO(EmbedDTO embedDTO) {
        this.title = embedDTO.getTitle();
        this.description = embedDTO.getDescription();
        this.url = embedDTO.getUrl();
        this.color = embedDTO.getColor();
        this.timestamp = embedDTO.getTimestamp();
        this.footer = new Footer(embedDTO.getFooter());
        this.thumbnail = new Thumbnail(embedDTO.getThumbnail());
        this.image = new Image(embedDTO.getImage());
        this.author = new Author(embedDTO.getAuthor());
        if (embedDTO.getFields() != null) {
            this.fields = new Field[embedDTO.getFields().length];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = new Field(embedDTO.getFields()[i]);
            }
        }
    }

    @Override
    public String toString() {
        return "Embed{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", color=" + color +
                ", timestamp='" + timestamp + '\'' +
                ", footer=" + toString(footer) +
                ", thumbnail=" + toString(thumbnail) +
                ", image=" + toString(image) +
                ", author=" + toString(author) +
                ", fields=" + Arrays.toString(fields) +
                '}';
    }

    private String toString(Object object) {
        return object == null ? null : object.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    /**
     * Transfers this object to a {@code EmbedBuilder}.
     *
     * @return the {@code EmbedBuilder}
     */
    public EmbedBuilder toEmbedBuilder() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (title != null) {
            embedBuilder.setTitle(title, url);
        }
        if (description != null) {
            embedBuilder.setDescription(description);
        }
        if (color != null) {
            embedBuilder.setColor(Color.decode(color));
        }
        if (timestamp != null) {
            embedBuilder.setTimestamp(ZonedDateTime.parse(timestamp));
        }
        if (footer != null) {
            embedBuilder.setFooter(footer.getText(), footer.getIconUrl());
        }
        if (thumbnail != null) {
            embedBuilder.setThumbnail(thumbnail.getUrl());
        }
        if (image != null) {
            embedBuilder.setImage(image.getUrl());
        }
        if (author != null) {
            embedBuilder.setAuthor(author.getName(), author.getUrl(), author.getIconUrl());
        }
        if (fields != null) {
            for (Field field : fields) {
                embedBuilder.addField(field.getName(), field.getValue(), field.isInline());
            }
        }
        return embedBuilder;
    }

    /**
     * Transfers this object to a {@code MessageEmbed}.
     *
     * @return the {@code MessageEmbed}
     */
    public MessageEmbed toMessageEmbed() {
        return toEmbedBuilder().build();
    }

    /**
     * Transfers this object to a {@link net.dv8tion.jda.api.entities.Message}.
     *
     * @return the {@link net.dv8tion.jda.api.entities.Message}
     */
    public Message toMessage() {
        return new MessageBuilder().setEmbeds(toMessageEmbed()).build();
    }

    /**
     * Attempts to inject {@code {placeholders}} with the values of the given object fields. Therefore, the name of the
     * field must match the name of the {@code {placeholder}}.
     *
     * @param objects the objects to get the fields from
     * @return the current instance to use fluent interface
     */
    public EmbedDTO injectFields(Object... objects) {
        for (Object object : objects) {
            for (java.lang.reflect.Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    injectValue(field.getName(), field.get(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    /**
     * Attempts to inject {@code {placeholders}} with the given values.
     *
     * @param values a Map with all values to inject. Key: name of the placeholder. Value: the value to inject
     * @return the current instance to use fluent interface
     */
    public EmbedDTO injectValues(Map<String, Object> values) {
        values.forEach(this::injectValue);
        return this;
    }

    /**
     * Attempts to inject a {@code {placeholder}} with the given value.
     *
     * @param name   the name of the placeholder
     * @param object the value to inject
     * @return the current instance to use fluent interface
     */
    public EmbedDTO injectValue(String name, Object object) {
        if (title != null) {
            title = title.replaceAll(pattern(name), replacement(object));
        }
        if (description != null) {
            description = description.replaceAll(pattern(name), replacement(object));
        }
        if (url != null) {
            url = url.replaceAll(pattern(name), replacement(object));
        }
        if (color != null) {
            color = color.replaceAll(pattern(name), replacement(object));
        }
        if (timestamp != null) {
            timestamp = timestamp.replaceAll(pattern(name), replacement(object));
        }
        if (footer != null) {
            if (footer.iconUrl != null) {
                footer.iconUrl = footer.iconUrl.replaceAll(pattern(name), replacement(object));
            }
            if (footer.text != null) {
                footer.text = footer.text.replaceAll(pattern(name), replacement(object));
            }
        }
        if (thumbnail != null) {
            if (thumbnail.url != null) {
                thumbnail.url = thumbnail.url.replaceAll(pattern(name), replacement(object));
            }
        }
        if (image != null) {
            if (image.url != null) {
                image.url = image.url.replaceAll(pattern(name), replacement(object));
            }
        }
        if (author != null) {
            if (author.iconUrl != null) {
                author.iconUrl = author.iconUrl.replaceAll(pattern(name), replacement(object));
            }
            if (author.name != null) {
                author.name = author.name.replaceAll(pattern(name), replacement(object));
            }
            if (author.url != null) {
                author.url = author.url.replaceAll(pattern(name), replacement(object));
            }
        }
        if (fields != null) {
            for (Field field : fields) {
                if (field.name != null) {
                    field.name = field.name.replaceAll(pattern(name), replacement(object));
                }
                if (field.value != null) {
                    field.value = field.value.replaceAll(pattern(name), replacement(object));
                }
            }
        }
        return this;
    }

    private String pattern(String name) {
        return String.format(Pattern.quote("{%s}"), name);
    }

    private String replacement(Object object) {
        return Matcher.quoteReplacement(String.valueOf(object));
    }

    /**
     * Attempts to format all fields with the given arguments. Be aware that this might only be useful for some fields
     * such as title, description or fields.
     *
     * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *             format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *             zero.
     * @return the current instance to use fluent interface
     */
    public EmbedDTO injectFormat(Object... args) {
        if (title != null) {
            title = String.format(title, args);
        }
        if (description != null) {
            description = String.format(description, args);
        }
        if (url != null) {
            url = String.format(url, args);
        }
        if (color != null) {
            color = String.format(color, args);
        }
        if (timestamp != null) {
            timestamp = String.format(timestamp, args);
        }
        if (footer != null) {
            if (footer.iconUrl != null) {
                footer.iconUrl = String.format(footer.iconUrl, args);
            }
            if (footer.text != null) {
                footer.text = String.format(footer.text, args);
            }
        }
        if (thumbnail != null) {
            if (thumbnail.url != null) {
                thumbnail.url = String.format(thumbnail.url, args);
            }
        }
        if (image != null) {
            if (image.url != null) {
                image.url = String.format(image.url, args);
            }
        }
        if (author != null) {
            if (author.iconUrl != null) {
                author.iconUrl = String.format(author.iconUrl, args);
            }
            if (author.name != null) {
                author.name = String.format(author.name, args);
            }
            if (author.url != null) {
                author.url = String.format(author.url, args);
            }
        }
        if (fields != null) {
            for (Field field : fields) {
                if (field.name != null) {
                    field.name = String.format(field.name, args);
                }
                if (field.value != null) {
                    field.value = String.format(field.value, args);
                }
            }
        }
        return this;
    }

    public static class Footer {
        private String iconUrl;
        private String text;

        public Footer(String iconUrl, String text) {
            this.iconUrl = iconUrl;
            this.text = text;
        }

        public Footer(Footer footer) {
            if (footer != null) {
                this.iconUrl = footer.getIconUrl();
                this.text = footer.getText();
            }
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "Footer{" +
                    "iconUrl='" + iconUrl + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    public static class Thumbnail {
        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }

        public Thumbnail(Thumbnail thumbnail) {
            if (thumbnail != null) {
                this.url = thumbnail.getUrl();
            }
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Thumbnail{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    public static class Image {
        private String url;

        public Image(String url) {
            this.url = url;
        }

        public Image(Image image) {
            if (image != null) {
                this.url = image.getUrl();
            }
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    public static class Author {
        private String name;
        private String url;
        private String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }

        public Author(Author author) {
            if (author != null) {
                this.name = author.getName();
                this.url = author.getUrl();
                this.iconUrl = author.getIconUrl();
            }
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        @Override
        public String toString() {
            return "Author{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    '}';
        }
    }

    public static class Field {
        private String name;
        private String value;
        private boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public Field(Field field) {
            if (field != null) {
                this.name = field.getName();
                this.value = field.getValue();
                this.inline = field.isInline();
            }
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", inline=" + inline +
                    '}';
        }
    }
}
