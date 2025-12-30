package messages;

import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public class TestHelpers {
    public static String resolve(String content, Map<String, @Nullable Object> placeholders) {
        return new PlaceholderResolver().resolve(content, Locale.ENGLISH, placeholders);
    }
}
