package io.github.kaktushose.jdac.message.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kaktushose.jdac.exceptions.ParsingException;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.internal.Resolver;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class EmbedResolver extends Resolver<DataObject, MessageEmbed> {

    public EmbedResolver(MessageResolver resolver) {
        super(resolver, Set.of());
    }

    @Override
    public MessageEmbed resolve(DataObject embed, Locale locale, Map<String, @Nullable Object> placeholders) {
        try {
            JsonNode node = resolve(mapper.readTree(embed.toData().toString()), locale, placeholders);
            return EmbedBuilder.fromData(DataObject.fromJson(node.toString())).build();
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", embed));
        }
    }
}
