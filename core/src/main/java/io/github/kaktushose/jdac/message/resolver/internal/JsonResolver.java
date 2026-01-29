package io.github.kaktushose.jdac.message.resolver.internal;

import io.github.kaktushose.jdac.message.resolver.Resolver;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
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
            objectNode.forEachEntry((key, entry) -> {
                JsonNode newChild = resolve(entry, locale, placeholders);
                if (newChild.isString() && resolve(key)) {
                    objectNode.put(key, resolver.resolve(newChild.asString(), locale, placeholders));
                } else {
                    objectNode.set(key, newChild);
                }
            });
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
