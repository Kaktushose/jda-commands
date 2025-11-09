package messages;

import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class EmojiResolverTest {

    @Test
    void unicode_not_found_emoji() {
        String text = ":not_found:";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals(":not_found:", resolved);
    }

    @Test
    void unicode_only_emoji() {
        String text = ":joy:";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("\uD83D\uDE02", resolved);
    }

    @Test
    void unicode_leading_emoji() {
        String text = ":joy: Rest";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("\uD83D\uDE02 Rest", resolved);
    }

    @Test
    void unicode_closing_emoji() {
        String text = "Rest :joy:";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("Rest \uD83D\uDE02", resolved);
    }

    @Test
    void unicode_enclosed_emoji() {
        String text = "Rest :joy: Rest2";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("Rest \uD83D\uDE02 Rest2", resolved);
    }

    @Test
    void unicode_emoji_with_skintone() {
        String text = ":woman_swimming::skin-tone-5:";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("\uD83C\uDFCA\uD83C\uDFFF\u200D♀\uFE0F", resolved);
    }

    @Test
    void unicode_closing_emoji_with_skintone_() {
        String text = "Rest :woman_swimming::skin-tone-5:";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("Rest \uD83C\uDFCA\uD83C\uDFFF\u200D♀\uFE0F", resolved);
    }

    @Test
    void unicode_escaped() {
        String text = "Hi\\: :joy:";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("Hi: \uD83D\uDE02", resolved);
    }

    @Test
    void unicode_non_escaped_colon() {
        String text = "Hi: :joy: huhu:: lolo escaped\\: hehe";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("Hi: \uD83D\uDE02 huhu:: lolo escaped: hehe", resolved);
    }

    @Test
    void single_colon_at_end_with_whitespace() {
        String text = "Currently following channels are registered as release channels: ";
        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("Currently following channels are registered as release channels: ", resolved);
    }

    @Test
    void multiline() {
        String text = """
                Currently following channels are registered as release channels:\s
                - this one :joy:
                - and this not :not_joy:
               """;


        String resolved = new EmojiResolver(List.of()).resolve(text);
        Assertions.assertEquals("""
                Currently following channels are registered as release channels:\s
                - this one 😂
                - and this not :not_joy:
               """, resolved);
    }
}
