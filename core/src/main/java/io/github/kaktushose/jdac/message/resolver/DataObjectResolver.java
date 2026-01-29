package io.github.kaktushose.jdac.message.resolver;

import io.github.kaktushose.jdac.exceptions.ParsingException;
import io.github.kaktushose.jdac.message.resolver.internal.JsonResolver;
import net.dv8tion.jda.api.utils.data.DataObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// [Resolver] implementation that is capable of resolving JDAs [DataObject].
///
/// Will traverse the [DataObject] down to every textual JSON node and will resolve all the fields that were configured
/// at the constructor.
///
/// ```
/// DataObject object = DataObject.fromJson("""
///         {
///             "resolve": "my-key",
///             "skip": "not resolved"
///         }
///         """);
/// DataObjectResolver objectResolver = new DataObjectResolver(messageResolver, Set.of("resolve"));
/// object = objectResolver.resolve(object, Locale.ENGLISH, Map.of());
/// ```
///
/// Note that many JDA objects can be directly serialized as and deserialized from a [DataObject].
public final class DataObjectResolver implements Resolver<DataObject> {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final Resolver<JsonNode> jsonResolver;

    /// Constructs a new DataObjectResolver.
    ///
    /// @param resolver a [Resolver] capable of resolving [String]s
    /// @param fields   the JSON fields to resolve. An empty [Set] indicates that all fields will be resolved
    public DataObjectResolver(Resolver<String> resolver, Set<String> fields) {
        jsonResolver = new JsonResolver(resolver, fields);
    }

    /// Resolves a [DataObject].
    ///
    /// @param object       the [DataObject] to resolve
    /// @param locale       the [Locale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used String [Resolver]
    /// @return the resolved [DataObject]
    @Override
    public DataObject resolve(DataObject object, Locale locale, Map<String, @Nullable Object> placeholders) {
        try {
            JsonNode node = jsonResolver.resolve(mapper.readTree(object.toData().toString()), locale, placeholders);
            return DataObject.fromJson(node.toString());
        } catch (Exception e) {
            throw new ParsingException(e, entry("rawJson", object));
        }
    }

    /// @return 0
    @Override
    public int priority() {
        return 0;
    }
}
