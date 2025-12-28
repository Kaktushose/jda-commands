package io.github.kaktushose.jdac.message.resolver;

import io.github.kaktushose.jdac.exceptions.ParsingException;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class ComponentResolver<T extends Component> implements Resolver<T> {

    private static final ComponentSerializer serializer = new ComponentSerializer();
    private final DataObjectResolver jsonResolver;

    private final Class<T> type;

    public ComponentResolver(Resolver<String> resolver, Class<T> type) {
        this.type = type;
        jsonResolver = new DataObjectResolver(resolver, Set.of("content", "label", "placeholder", "value"));
    }

    public List<T> resolve(Collection<T> components, Locale locale, Map<String, @Nullable Object> placeholders) {
        return components.stream().map(it -> resolve(it, locale, placeholders)).toList();
    }

    @Override
    public T resolve(T component, Locale locale, Map<String, @Nullable Object> placeholders) {
        List<FileUpload> fileUploads = serializer.getFileUploads(component);
        DataObject data = serializer.serialize(component);
        try {
            data = jsonResolver.resolve(data, locale, placeholders);
            ComponentDeserializer deserializer = new ComponentDeserializer(fileUploads);
            return deserializer.deserializeAs(type, data);
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", data.toString()));
        }
    }
}
