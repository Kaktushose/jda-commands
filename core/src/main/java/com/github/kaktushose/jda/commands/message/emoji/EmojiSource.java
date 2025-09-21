package com.github.kaktushose.jda.commands.message.emoji;

import com.github.kaktushose.jda.commands.exceptions.ConfigurationException;
import com.github.kaktushose.jda.commands.extension.Implementation;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.entities.Icon;
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

import static com.github.kaktushose.jda.commands.message.i18n.I18n.entry;

public non-sealed interface EmojiSource extends Implementation.ExtensionProvidable {

    Pattern RESOURCE_PATTERN = Pattern.compile("emoji/(.*)[.].*");

    Logger log = LoggerFactory.getLogger(EmojiSource.class);

    static EmojiSource reflective(String... paths) {
        record EmojiFile(Resource resource, String name) {}

        String[] acceptedPaths = paths.length == 0
                ? new String[]{"emoji"}
                : paths;

        return () -> {
            try (ScanResult result = new ClassGraph()
                    .acceptPaths(acceptedPaths)
                    .scan()) {

                ResourceList allResources = result.getAllResources();
                return allResources.stream()
                        .map(resource -> {
                            String path = resource.getPath();
                            Matcher matcher = RESOURCE_PATTERN.matcher(path);
                            if (!matcher.matches()) return null;
                            return new EmojiFile(resource, matcher.group(1));
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(EmojiFile::name, file -> {
                            try (Resource resource = file.resource; InputStream i = resource.open()) {
                                return Icon.from(i);
                            } catch (IOException e) {
                                throw new ConfigurationException("emoji-not-loadable-from-resource", e, entry("name", file.name), entry("path", file.resource.getPath()));
                            }
                        }));
            }
        };
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
