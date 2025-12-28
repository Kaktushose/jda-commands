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

/// [Resolver] implementation that can resolve any [Component]. This will only resolve the textual fields of components,
/// namely:
///
/// - `content`
/// - `label`
/// - `placeholder`
/// - `value`
///
/// As with other [Resolver]s, this class is not intended to be directly used by end users but part of the public api
/// to allow manual execution of the frameworks resolving logic for dynamic values if needed.
///
/// @param <T> a subtype of [Component] to resolve
public final class ComponentResolver<T extends Component> implements Resolver<T> {

    private static final ComponentSerializer serializer = new ComponentSerializer();
    private final DataObjectResolver jsonResolver;

    private final Class<T> type;

    /// Constructs a new ComponentResolver.
    ///
    /// @param resolver a [Resolver] capable of resolving [String]s
    /// @param type     the [Component] [Class], needed for deserialization
    public ComponentResolver(Resolver<String> resolver, Class<T> type) {
        this.type = type;
        jsonResolver = new DataObjectResolver(resolver, Set.of("content", "label", "placeholder", "value"));
    }

    /// Resolves a [Collection] of [Component]s. [FileUpload]s will be preserved.
    ///
    /// @param components   the [Component]s to resolve
    /// @param locale       the [Locale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used String [Resolver]
    /// @return the [Collection] of resolved [Component]s
    public Collection<T> resolve(Collection<T> components, Locale locale, Map<String, @Nullable Object> placeholders) {
        return components.stream().map(it -> resolve(it, locale, placeholders)).toList();
    }

    /// Resolves a [Component]. [FileUpload]s will be preserved.
    ///
    /// @param component    the [Component] to resolve
    /// @param locale       the [Locale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used String [Resolver]
    /// @return the resolved [Component]
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
