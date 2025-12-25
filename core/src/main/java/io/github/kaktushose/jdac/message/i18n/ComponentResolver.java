package io.github.kaktushose.jdac.message.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kaktushose.jdac.exceptions.ParsingException;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.internal.Resolver;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class ComponentResolver<T extends Component> extends Resolver<T> {

    private static final ComponentSerializer serializer = new ComponentSerializer();
    private final Class<T> type;

    public ComponentResolver(MessageResolver resolver, Class<T> type) {
        super(resolver, Set.of("content", "label", "placeholder", "value"));
        this.type = type;
    }

    public List<T> resolve(Collection<T> components, Locale locale, Map<String, @Nullable Object> placeholders) {
        List<T> result = new ArrayList<>();

        for (T component : components) {
            result.add(resolve(component, locale, placeholders));
        }

        return result;
    }

    @Override
    public T resolve(T component, Locale locale, Map<String, @Nullable Object> placeholders) {
        List<FileUpload> fileUploads = serializer.getFileUploads(component);
        DataObject data = serializer.serialize(component);
        try {
            JsonNode node = resolve(mapper.readTree(data.toString()), locale, placeholders);
            ComponentDeserializer deserializer = new ComponentDeserializer(fileUploads);
            return deserializer.deserializeAs(type, DataObject.fromJson(node.toString()));
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", data.toString()));
        }
    }
}
