package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Embed extends EmbedBuilder {

    public Embed(EmbedBuilder embedBuilder) {
        super(embedBuilder);
    }

    // TODO fields API

    public Embed placeholder(String placeholder, Object value) {
        DataObject object = build().toData();
        String json = object.toString();
        json = json.replaceAll(
                String.format(Pattern.quote("{%s}"), Matcher.quoteReplacement(placeholder)),
                Matcher.quoteReplacement(String.valueOf(value))
        );
        return new Embed(EmbedBuilder.fromData(DataObject.fromJson(json)));
    }

    public Embed placeholder(Map<String, Object> values) {
        values.forEach(this::placeholder);
        return this;
    }

    @NotNull
    public MessageCreateData toMessageCreateData() {
        return MessageCreateData.fromEmbeds(build());
    }

    @NotNull
    public MessageEditData toMessageEditData() {
        return MessageEditData.fromEmbeds(build());
    }

}
