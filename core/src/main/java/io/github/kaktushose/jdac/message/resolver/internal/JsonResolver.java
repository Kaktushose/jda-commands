package io.github.kaktushose.jdac.message.resolver.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public final class JsonResolver implements Resolver<JsonNode> {

    private final Resolver<String> resolver;
    private final Set<String> fields;

    public JsonResolver(Resolver<String> resolver, Set<String> fields) {
        this.resolver = resolver;
        this.fields = fields;
    }

    @Override
    public JsonNode resolve(JsonNode node, Locale locale, Map<String, @Nullable Object> placeholders) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                JsonNode child = entry.getValue();
                JsonNode newChild = resolve(child, locale, placeholders);
                if (newChild.isTextual() && resolve(entry.getKey())) {
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

    /// @return 0
    @Override
    public int priority() {
        return 0;
    }

    private boolean resolve(String key) {
        if (fields.isEmpty()) {
            return true;
        }
        return fields.contains(key);
    }
}
