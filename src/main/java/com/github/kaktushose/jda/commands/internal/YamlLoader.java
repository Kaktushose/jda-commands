package com.github.kaktushose.jda.commands.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import com.github.kaktushose.jda.commands.entities.JDACommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * This class contains methods to create a {@link CommandSettings} object
 * from a yaml file.
 *
 * <p>To be loaded properly the yaml file must be located in the resources folder and its name must be
 * <em>settings.yaml</em>. If the yaml file deviates from that, this class can be used to load the yaml file manually.
 * Apart from that this class has only an internal purpose and can be ignored.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public final class YamlLoader {
    /**
     * Loads a yaml file and creates a {@link CommandSettings} object from it.
     *
     * @param url The URL where the yaml file is located
     * @return The {@link CommandSettings} object
     * @throws IOException If an I/O error occurs
     */
    public static CommandSettings load(URL url) throws IOException, ClassNotFoundException {
        return load(new File(url.getFile()));
    }

    /**
     * Loads a yaml file and creates a {@link CommandSettings} object from it.
     *
     * @param pathName The name of the path where the yaml file is located
     * @return The {@link CommandSettings} object
     * @throws IOException If an I/O error occurs
     */
    public static CommandSettings load(String pathName) throws IOException, ClassNotFoundException {
        return load(new File(pathName));
    }

    /**
     * Loads a yaml file and creates a {@link CommandSettings} object from it.
     *
     * @param file The yaml File to be loaded
     * @return The {@link CommandSettings} object
     * @throws IOException If an I/O error occurs
     */
    public static CommandSettings load(File file) throws IOException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, CommandSettings.class);
    }

    public static CommandSettings loadContent(String content) throws IOException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(content, CommandSettings.class);
    }

}
