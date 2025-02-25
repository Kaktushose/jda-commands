package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Embed {

    private final EmbedBuilder embedBuilder;

    public Embed(EmbedBuilder embedBuilder) {
        this.embedBuilder = embedBuilder;
    }

    public Embed title(String title) {
        embedBuilder.setTitle(title);
        return this;
    }


    public Embed title(String title, String url) {
        embedBuilder.setTitle(title, url);
        return this;
    }

    public Embed description(CharSequence description) {
        embedBuilder.setDescription(description);
        return this;
    }

    public Embed color(int color) {
        embedBuilder.setColor(color);
        return this;
    }

    public Embed color(Color color) {
        embedBuilder.setColor(color);
        return this;
    }

    public Embed timestamp(TemporalAccessor accessor) {
        embedBuilder.setTimestamp(accessor);
        return this;
    }

    public Embed footer(String footer) {
        embedBuilder.setFooter(footer);
        return this;
    }

    public Embed footer(String footer, String iconUrl) {
        embedBuilder.setFooter(footer, iconUrl);
        return this;
    }

    public Embed thumbnail(String url) {
        embedBuilder.setThumbnail(url);
        return this;
    }

    public Embed image(String url) {
        embedBuilder.setImage(url);
        return this;
    }

    public Embed author(String name) {
        embedBuilder.setAuthor(name);
        return this;
    }

    public Embed author(String name, String url) {
        embedBuilder.setAuthor(name, url);
        return this;
    }

    public Embed author(String name, String url, String iconUrl) {
        embedBuilder.setAuthor(name, url, iconUrl);
        return this;
    }

    // TODO fields API

    public Embed placeholder(String placeholder, Object value) {
        DataObject object = embedBuilder.build().toData();
        String json = object.toString();
        json = json.replaceAll(
                String.format(Pattern.quote("{%s}"), Matcher.quoteReplacement(placeholder)),
                String.valueOf(value)
        );
        return new Embed(EmbedBuilder.fromData(DataObject.fromJson(json)));
    }

    public Embed placeholder(Map<String, Object> values) {
        values.forEach(this::placeholder);
        return this;
    }

    public @NotNull MessageCreateData toMessageCreateData() {
        return MessageCreateData.fromEmbeds(embedBuilder.build());
    }

    public MessageEmbed build() {
        return embedBuilder.build();
    }
}
