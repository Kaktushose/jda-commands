package com.github.kaktushose.jda.commands.message.variables;

import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VariableResolver {

    private static final Collection<String> forbiddenCharacters = List.of(
            " ",
            "\n",
            "{",
            "$"
    );

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
