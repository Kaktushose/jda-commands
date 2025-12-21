package io.github.kaktushose.jdac.message.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kaktushose.jdac.exceptions.ParsingException;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.internal.Localizer;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class ComponentLocalizer extends Localizer<MessageTopLevelComponentUnion> {

    private static final ComponentSerializer serializer = new ComponentSerializer();

    public ComponentLocalizer(MessageResolver resolver) {
        super(resolver, Set.of("content"));
    }

    public List<MessageTopLevelComponentUnion> localize(Collection<MessageTopLevelComponentUnion> components, Locale locale, Map<String, @Nullable Object> placeholders) {
        List<MessageTopLevelComponentUnion> result = new ArrayList<>();

        for (MessageTopLevelComponentUnion component : components) {
            result.add(localize(component, locale, placeholders));
        }

        return result;
    }

    @Override
    public MessageTopLevelComponentUnion localize(MessageTopLevelComponentUnion component, Locale locale, Map<String, @Nullable Object> placeholders) {
        List<FileUpload> fileUploads = serializer.getFileUploads(component);
        DataObject data = serializer.serialize(component);
        try {
            JsonNode node = resolve(mapper.readTree(data.toString()), locale, placeholders);
            ComponentDeserializer deserializer = new ComponentDeserializer(fileUploads);
            return deserializer.deserializeAs(MessageTopLevelComponentUnion.class, DataObject.fromJson(node.toString()));
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", data.toString()));
        }
    }
}
