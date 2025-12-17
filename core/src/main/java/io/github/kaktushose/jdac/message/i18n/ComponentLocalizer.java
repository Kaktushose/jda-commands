package io.github.kaktushose.jdac.message.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kaktushose.jdac.exceptions.ParsingException;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public class ComponentLocalizer {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ComponentSerializer serializer = new ComponentSerializer();
    private final MessageResolver resolver;

    public ComponentLocalizer(MessageResolver resolver) {
        this.resolver = resolver;
    }

    public List<MessageTopLevelComponentUnion> localize(Collection<MessageTopLevelComponentUnion> components, Locale locale, Entry... placeholder) {
        List<MessageTopLevelComponentUnion> result = new ArrayList<>();

        for (MessageTopLevelComponentUnion component : components) {
            result.add(localize(component, locale, placeholder));
        }

        return result;
    }

    private MessageTopLevelComponentUnion localize(MessageTopLevelComponentUnion component, Locale locale, Entry... placeholder) {
        List<FileUpload> fileUploads = serializer.getFileUploads(component);
        DataObject data = serializer.serialize(component);
        try {
            JsonNode node = resolve(mapper.readTree(data.toString()), locale, placeholder);
            ComponentDeserializer deserializer = new ComponentDeserializer(fileUploads);
            return deserializer.deserializeAs(MessageTopLevelComponentUnion.class, DataObject.fromJson(node.toString()));
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", data.toString()));
        }
    }

    private JsonNode resolve(JsonNode node, Locale locale, Entry... placeholder) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                JsonNode child = entry.getValue();
                JsonNode newChild = resolve(child, locale, placeholder);
                if (newChild.isTextual() && "content".equals(entry.getKey())) {
                    objectNode.put(entry.getKey(), resolver.resolve(newChild.asText(), locale, placeholder));
                } else {
                    objectNode.set(entry.getKey(), newChild);
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode child = arrayNode.get(i);
                arrayNode.set(i, resolve(child, locale, placeholder));
            }
        }
        return node;
    }
}
