package com.github.kaktushose.jda.commands.util;

import com.github.kaktushose.jda.commands.entities.CommandList;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class automatically generates documentation for commands in markdown format.
 *
 * @author Kaktushose
 * @version 1.1.0
 * @since 1.1.0
 */
public class CommandDocumentation {

    private static final Logger log = LoggerFactory.getLogger(CommandDocumentation.class);
    private final CommandList commandList;
    private final StringBuilder docs;
    private final String prefixPattern;
    private final String prefix;

    /**
     * Creates a new CommandDocumentation.
     *
     * @param commandList   the {@link CommandList} to create the documenation for
     * @param prefixPattern the prefix placeholder that is used in
     *                      {@link com.github.kaktushose.jda.commands.annotations.Command} annotations. The default value is {@code {prefix}}
     * @param prefix        the prefix to replace the pattern with
     */
    public CommandDocumentation(@Nonnull CommandList commandList, @Nonnull String prefixPattern, @Nonnull String prefix) {
        this.commandList = commandList;
        this.prefixPattern = prefixPattern;
        this.prefix = prefix;
        docs = new StringBuilder();
    }

    /**
     * Generates the markdown.
     */
    public void generate() {
        docs.append(String.format("> Auto generated command manual | %s",
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()))
        ).append("\n\n");

        commandList.getSortedByCategories().forEach(((category, commandCallables) -> {
            docs.append(new Heading(replace(category), 1)).append("\n");
            commandCallables.forEach(commandCallable -> {
                docs.append(new Heading(replace(commandCallable.getName()), 3)).append("\n\n");
                docs.append(new BoldText("Description:")).append("\n\n");
                docs.append(replace(commandCallable.getDescription())).append("\n\n");
                docs.append(new BoldText("Usage:")).append("\n\n");
                docs.append(String.format("`%s`", replace(commandCallable.getUsage()))).append("\n\n");
                List<String> labels = commandCallable.getLabels().stream().skip(1).collect(Collectors.toList());
                if (labels.size() > 0) {
                    docs.append(new BoldText(("Aliases:"))).append("\n\n");
                    docs.append(new UnorderedList<>(labels)).append("\n\n");
                }
                docs.append(new BoldText("Permissions:")).append("\n\n");
                docs.append(new UnorderedList<>(new ArrayList<>(commandCallable.getPermissions()))).append("\n\n");
            });
        }));
    }

    /**
     * Writes the markdown output into a file.
     *
     * @param file the file to save the markdown in
     */
    public void saveToFile(File file) {
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.write(docs.toString());
        } catch (FileNotFoundException e) {
            log.error("Unable to write to file", e);
        }
    }

    /**
     * Gets the String containing the markdown.
     *
     * @return the markdown string
     */
    public String toString() {
        return docs.toString();
    }

    private String replace(String raw) {
        return raw.replaceAll(Pattern.quote(prefixPattern), prefix);
    }
}
