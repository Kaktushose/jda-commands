package io.github.kaktushose.jdac.components;


import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.message.MessageResolver;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@FunctionalInterface
public interface ComponentDataSource {

    static ComponentDataSource json(String json) {
        return dataObject(DataObject.fromJson(json));
    }

    static ComponentDataSource resource(String resource) {
        try (InputStream inputStream = ComponentDataSource.class.getClassLoader().getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new ConfigurationException("resource-not-found", entry("resource", resource));
            }
            return inputStream(inputStream);
        } catch (IOException e) {
            throw new ConfigurationException("io-exception", e);
        }
    }

    static ComponentDataSource file(Path path) {
        try {
            return json(Files.readString(path));
        } catch (IOException e) {
            throw new ConfigurationException("io-exception", e);
        }
    }

    static ComponentDataSource inputStream(InputStream inputStream) {
        return dataObject(DataObject.fromJson(inputStream));
    }

    static ComponentDataSource dataObject(DataObject dataObject) {
        return (component, placeholders, messageResolver) -> {
            // create copy so modifications don't affect the source DataObject
            DataObject copy = DataObject.fromJson(dataObject.toJson());
            if (!copy.hasKey(component)) {
                return Optional.empty();
            }
            return Optional.of(ComponentDto.of(copy.getObject(component), component, placeholders, messageResolver));
        };
    }

    Optional<MessageTopLevelComponent> get(String component, Map<String, Object> placeholders, MessageResolver messageResolver);
}
