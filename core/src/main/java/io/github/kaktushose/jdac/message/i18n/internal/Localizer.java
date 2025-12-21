package io.github.kaktushose.jdac.message.i18n.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.ComponentLocalizer;
import io.github.kaktushose.jdac.message.i18n.EmbedLocalizer;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public abstract sealed class Localizer<T> permits ComponentLocalizer, EmbedLocalizer {

    protected static final ObjectMapper mapper = new ObjectMapper();
    private final MessageResolver resolver;
    private final Set<String> fields;

    /// Constructs a new Localizer.
    ///
    /// @param resolver the [MessageResolver] to use for localization
    /// @param fields the JSON fields to localize.  An empty [Set] indicates that all fields will be localized
    public Localizer(MessageResolver resolver, Set<String> fields) {
        this.resolver = resolver;
        this.fields = fields;
    }

    public abstract T localize(T object, Locale locale, Map<String, @Nullable Object> placeholders);

    protected JsonNode resolve(JsonNode node, Locale locale, Map<String, @Nullable Object> placeholders) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                JsonNode child = entry.getValue();
                JsonNode newChild = resolve(child, locale, placeholders);
                if (newChild.isTextual() && localize(entry.getKey())) {
                    objectNode.put(entry.getKey(), resolver.resolve(newChild.asText(), locale, placeholders));
                } else {
                    objectNode.set(entry.getKey(), newChild);
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode child = arrayNode.get(i);
                arrayNode.set(i, resolve(child, locale, placeholders));
            }
        }
        return node;
    }

    private boolean localize(String key) {
        if (fields.isEmpty()) {
            return true;
        }
        return fields.contains(key);
    }
}
