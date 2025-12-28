package io.github.kaktushose.jdac.message.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kaktushose.jdac.exceptions.ParsingException;
import io.github.kaktushose.jdac.message.resolver.internal.JsonResolver;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class DataObjectResolver implements Resolver<DataObject> {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final Resolver<JsonNode> jsonResolver;

    public DataObjectResolver(Resolver<String> resolver, Set<String> fields) {
        jsonResolver = new JsonResolver(resolver, fields);
    }

    @Override
    public DataObject resolve(DataObject embed, Locale locale, Map<String, @Nullable Object> placeholders) {
        try {
            JsonNode node = jsonResolver.resolve(mapper.readTree(embed.toData().toString()), locale, placeholders);
            return DataObject.fromJson(node.toString());
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", embed));
        }
    }
}
