package io.github.kaktushose.jdac.message.emoji;

import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import net.dv8tion.jda.api.entities.Icon;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Emojis loaded from an implementation of [EmojiSource] are automatically registered upon startup.
///
/// Per default all emojis contained in the directory "emojis" in the resource folder are registered automatically.
public interface EmojiSource {

    Pattern RESOURCE_PATTERN = Pattern.compile(".*/(.*)[.].*");

    Logger log = LoggerFactory.getLogger(EmojiSource.class);

    /// This implementation of [EmojiSource] scans the classpath for emoji files under the given paths. (resource
    /// directory names)
    /// The file name will be used as the emoji name.
    ///
    /// If no path is passed as an argument, the default path "emojis" will be used.
    ///
    /// @param paths the paths to scan (resource directories)
    static EmojiSource reflective(String... paths) {
        record EmojiFile(Resource resource, String name) { }

        String[] acceptedPaths = paths.length == 0
                ? new String[]{"emojis"}
                : paths;

        return () -> {
            try (ScanResult result = new ClassGraph()
                    .acceptPaths(acceptedPaths)
                    .scan()
            ) {

                ResourceList allResources = result.getAllResources();
                return allResources.stream()
                        .map(resource -> {
                            String path = resource.getPath();
                            Matcher matcher = RESOURCE_PATTERN.matcher(path);
                            if (!matcher.matches()) {
                                return null;
                            }
                            return new EmojiFile(resource, matcher.group(1));
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                EmojiFile::name, file -> {
                                    try (Resource resource = file.resource; InputStream i = resource.open()) {
                                        return Icon.from(i);
                                    } catch (IOException e) {
                                        throw new ConfigurationException(
                                                "emoji-not-loadable-from-resource", e,
                                                entry("name", file.name), entry(
                                                "path",
                                                file.resource.getPath()
                                        )
                                        );
                                    }
                                }
                        ));
            }
        };
    }

    /// Loads an emoji from a given URL.
    ///
    /// @param name the name of the emoji
    /// @param url  the [URL] the emoji should be loaded from
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

    /// Loads an emoji from a given [Icon] instance, allowing interop with JDAs system
    ///
    /// @param name the emojis name
    /// @param icon the emojis [Icon]
    static EmojiSource fromIcon(String name, Icon icon) {
        return () -> Map.of(name, icon);
    }

    /// This method is called during startup to load the to be registered application emojis.
    ///
    /// @return a map, mapping the emojis name to it's [Icon] instance
    /// @apiNote This method will be called blocking and sequentially, I/O will therefore delay startup.
    Map<String, Icon> get();
}
