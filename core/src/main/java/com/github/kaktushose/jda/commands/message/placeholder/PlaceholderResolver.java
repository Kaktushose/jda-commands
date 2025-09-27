package com.github.kaktushose.jda.commands.message.placeholder;

import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/// The placeholder resolver is used to do simple placeholder/variable resolution.
/// JDA-Commands uses a format similar to [project fluent](https://projectfluent.org/fluent/guide/) but with some
/// restrictions.
///
/// The overall placeholder format is `{ $your_placeholder }`, with following properties:
///
/// - the leading `$` is optional
/// - whitespace, newlines, `{` and `$` are forbidden inside the reference name
/// - trailing and leading whitespace or newline of the reference name is trimmed (see [String#trim()]).
/// - to escape the `{` character just prefix it with `\\` (backslashes can be used _unescaped_ in the rest of the string)
///
/// Invalid placeholders will just be treated as literal text.
///
/// To get a variables string representation, this resolver:
///
/// 1. calls [Proteus#convert(Object, Type, Type)] trying to convert the values to [`Type.of(String.class)`](Type#of(Class))
/// 2. if not successful, just calls [Object#toString()]
///
/// If a variable couldn't be found, `null` will be inserted.
public class PlaceholderResolver {

    private static final Collection<String> forbiddenCharacters = List.of(
            " ",
            "\n",
            "{",
            "$"
    );

    /// Resolves the given string according to the class docs.
    ///
    /// @param content the string to be resolved
    /// @param placeholder the placeholders to be used
    ///
    /// @return the string with placeholders replaced by their value
    public static String resolve(String content, Map<String, @Nullable Object> placeholder) {
        return parse(content)
                .stream()
                .map(component -> switch (component) {
                    case Component.Literal(String value) -> value;
                    case Component.VariableReference(String reference) -> getPlaceholder(reference, placeholder);
                })
                .collect(Collectors.joining());
    }

    private static String getPlaceholder(String name, Map<String, @Nullable Object> placeholder) {
        Object value = placeholder.get(name);
        if (value == null) return "null";
        ConversionResult<String> result = Proteus.global().convert(value, Type.dynamic(value), Type.of(String.class));

        return switch (result) {
            case ConversionResult.Success(String formatted, var _) -> formatted;
            case ConversionResult.Failure<?> _ -> value.toString();
        };
    }

    private static List<Component> parse(String msg) {
        List<Component> components = new ArrayList<>();

        int nextLiteralStart = 0;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);

            if (c == '{') {
                if (i >= 1 && msg.charAt(i - 1) == '\\') {
                    String literal = msg.substring(nextLiteralStart, i - 1);
                    components.add(new Component.Literal(literal));

                    nextLiteralStart = i;
                    continue;
                }

                String literal = msg.substring(nextLiteralStart, i);
                components.add(new Component.Literal(literal));


                int closingBracket = msg.indexOf('}', i);

                // no closing bracket found
                if (closingBracket == -1) {
                    nextLiteralStart = i;
                    continue;
                }

                // get whole reference
                String reference = msg.substring(i + 1, closingBracket).trim();

                // strip optional leading dollar sign
                if (reference.startsWith("$")) {
                    reference = reference.substring(1);
                }

                // check for forbidden characters or blank reference -> if found, treat as literal text
                if (reference.isBlank() || forbiddenCharacters.stream().anyMatch(reference::contains)) {
                    nextLiteralStart = i;
                    continue;
                }

                components.add(new Component.VariableReference(reference));

                i = closingBracket;
                nextLiteralStart = i+1;

            }

            if (i >= msg.length() - 1) {
                String literal = msg.substring(nextLiteralStart);
                components.add(new Component.Literal(literal));
            }
        }

        return components;
    }
}
