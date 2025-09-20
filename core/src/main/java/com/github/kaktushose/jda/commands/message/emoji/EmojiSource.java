package com.github.kaktushose.jda.commands.message.emoji;

import com.github.kaktushose.jda.commands.extension.Implementation;
import net.dv8tion.jda.api.entities.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public non-sealed interface EmojiSource extends Implementation.ExtensionProvidable {

    Logger log = LoggerFactory.getLogger(EmojiSource.class);

    static EmojiSource reflective(Class<?> klass, String... directories) {
        return () -> Map.of();
    }

    static EmojiSource fromUrl(String name, URL url) {
        return () -> {
            try (InputStream i = url.openStream()) {
                return Map.of(name, Icon.from(i));
            } catch (IOException e) {
                log.warn("Couldn't read emoji called '{}' from {}", name, url, e);
                return Map.of();
            }
        };
    }

    static EmojiSource fromIcon(String name, Icon icon) {
        return () -> Map.of(name, icon);
    }

    Map<String, Icon> get();
}
