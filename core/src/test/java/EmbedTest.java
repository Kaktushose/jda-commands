import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.i18n.FluavaLocalizer;
import com.github.kaktushose.jda.commands.i18n.I18n;
import dev.goldmensch.fluava.Fluava;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;

import static com.github.kaktushose.jda.commands.i18n.I18n.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmbedTest {

    private static I18n i18n;
    private static EmbedDataSource embedDataSource;
    private static MessageEmbed expected;

    @BeforeAll
    static void init() {
        embedDataSource = EmbedDataSource.resource("embeds.json");
        i18n = new I18n(Descriptor.REFLECTIVE, new FluavaLocalizer(new Fluava(Locale.ENGLISH)));
        expected = new EmbedBuilder()
                .setAuthor("Kaktushose", "https://cdn.discordapp.com/embed/avatars/0.png", "https://cdn.discordapp.com/embed/avatars/0.png")
                .setTitle("Test Title")
                .setDescription("Test Description")
                .addField("Test Field name", "Test Field value", false)
                .setThumbnail("https://cdn.discordapp.com/embed/avatars/0.png")
                .setImage("https://cdn.discordapp.com/embed/avatars/0.png")
                .setFooter("Footer", "https://cdn.discordapp.com/embed/avatars/0.png")
                .setTimestamp(OffsetDateTime.parse("2025-09-04T15:08:20Z"))
                .setColor(48028)
                .setUrl("https://discord.com")
                .build();
    }

    @Test
    void testFinalEmbed() {
        assertEquals(expected, embedDataSource.get("final", Map.of(), i18n).orElseThrow().build());
    }

    @Test
    void testPlaceholderEmbed() {
        Embed loaded = embedDataSource.get("placeholders", Map.of(), i18n).orElseThrow();

        MessageEmbed built = loaded.placeholders(
                entry("author-icon-url", "https://cdn.discordapp.com/embed/avatars/0.png"),
                entry("author-url", "https://cdn.discordapp.com/embed/avatars/0.png"),
                entry("author-name", "Kaktushose"),
                entry("title", "Test Title"),
                entry("description", "Test Description"),
                entry("fields-name", "Test Field name"),
                entry("fields-value", "Test Field value"),
                entry("thumbnail-url", "https://cdn.discordapp.com/embed/avatars/0.png"),
                entry("image-url", "https://cdn.discordapp.com/embed/avatars/0.png"),
                entry("footer-icon-url", "https://cdn.discordapp.com/embed/avatars/0.png"),
                entry("footer-text", "Footer"),
                entry("timestamp", "2025-09-04T15:08:20Z"),
                entry("color", "48028"),
                entry("url", "https://discord.com")
        ).build();

        assertEquals(expected, built);
    }

    @Test
    void testLocalizedEmbed() {
        assertEquals(expected, embedDataSource.get("i18n", Map.of(), i18n).orElseThrow().build());
    }

    @Test
    void testMinimalEmbed() {
        MessageEmbed minimum = new EmbedBuilder()
                .setTitle("Test Title")
                .setDescription("Test Description")
                .build();
        assertEquals(minimum, embedDataSource.get("minimum", Map.of(), i18n).orElseThrow().build());
    }
}
