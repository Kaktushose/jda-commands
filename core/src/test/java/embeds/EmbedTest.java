package embeds;

import io.github.kaktushose.jdac.annotations.i18n.Bundle;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.embeds.Embed;
import io.github.kaktushose.jdac.embeds.EmbedDataSource;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import dev.goldmensch.fluava.Fluava;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;
import static org.junit.jupiter.api.Assertions.*;

@Bundle("embeds")
class EmbedTest {

    private static Embeds embeds;
    private static MessageEmbed expected;

    @BeforeAll
    static void init() {
        EmbedDataSource embedDataSource = EmbedDataSource.resource("embeds/embeds.json");
        I18n i18n = new I18n(Descriptor.REFLECTIVE, new FluavaLocalizer(Fluava.create(Locale.ENGLISH)));
        MessageResolver messageResolver = new MessageResolver(List.of(new PlaceholderResolver(), i18n, new EmojiResolver(List.of())));
        embeds = new Embeds(List.of(embedDataSource), Map.of(), messageResolver);
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
    void testNoModification() {
        assertEquals(expected, embeds.get("final").build());
    }

    @Test
    void testDirectModification() {
        MessageEmbed expected = new EmbedBuilder()
                .setAuthor("Goldmensch", "https://cdn.discordapp.com/embed/avatars/1.png", "https://cdn.discordapp.com/embed/avatars/1.png")
                .setTitle("Test Title 2", "https://discord.com")
                .setDescription("Test Description 2")
                .addField("Test Field name 2", "Test Field value 2", false)
                .setThumbnail("https://cdn.discordapp.com/embed/avatars/1.png")
                .setImage("https://cdn.discordapp.com/embed/avatars/1.png")
                .setFooter("Footer", "https://cdn.discordapp.com/embed/avatars/1.png")
                .setTimestamp(OffsetDateTime.parse("2025-09-05T15:28:20Z"))
                .setColor(0)
                .build();
        Embed actual =  embeds.get("modification")
                .author("Goldmensch", "https://cdn.discordapp.com/embed/avatars/1.png", "https://cdn.discordapp.com/embed/avatars/1.png")
                .title("Test Title 2")
                .description("Test Description 2")
                .thumbnail("https://cdn.discordapp.com/embed/avatars/1.png")
                .image("https://cdn.discordapp.com/embed/avatars/1.png")
                .footer("Footer", "https://cdn.discordapp.com/embed/avatars/1.png")
                .timestamp(OffsetDateTime.parse("2025-09-05T15:28:20Z"))
                .color(0);
        actual.fields().replace(_ -> true, new Field("Test Field name 2", "Test Field value 2", false));
        assertEquals(expected, actual.build());
    }

    @Test
    void testPlaceholder() {
        Embed loaded = embeds.get("placeholders");

        Embed actual = loaded.placeholders(
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
        );

        assertEquals(expected, actual.build());
    }

    @Test
    void testLocalization() {
        assertEquals(expected, embeds.get("i18n").build());
    }

    @Test
    void testMinimalEmbed() {
        MessageEmbed expected = new EmbedBuilder().setTitle("Test Title").build();
        assertEquals(expected, embeds.get("minimum").build());
    }

    @Test
    void testColors() {
        Embed embed = embeds.get("color");

        embed.placeholders(entry("color", Color.RED));
        assertDoesNotThrow(embed::build);

        embed.placeholders(entry("color", "1000"));
        assertDoesNotThrow(embed::build);
    }

    @Test
    void testFieldsOrder() {
        MessageEmbed expected = new EmbedBuilder().setTitle("Test Title")
                .addField("5", "5", true)
                .addField("3", "3", false)
                .addField("4", "4", false)
                .addField("6", "6", false)
                .build();

        Embed actual = embeds.get("order");
        actual.fields()
                .remove("2")
                .replace("1", new Field("5", "5", true))
                .add("6", "6");

        assertEquals(expected, actual.build());
    }

    @Test
    void modificationsShouldNotModifySource() {
        Embed first = embeds.get("sourceModification");

        first.title("Modified Title");

        Embed second = embeds.get("sourceModification");
        assertEquals("Original Title", second.build().getTitle());
    }
}
