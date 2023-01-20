package data;

import com.github.kaktushose.jda.commands.data.CommandList;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandListTest {

    private static CommandList commandList;

    @BeforeAll
    public static void setup() throws NoSuchMethodException {
        CommandListTestController instance = new CommandListTestController();
        ValidatorRegistry validators = new ValidatorRegistry();
        TypeAdapterRegistry adapters = new TypeAdapterRegistry();
        commandList = new CommandList();

        commandList.add(SlashCommandDefinition.build(instance.getClass().getDeclaredMethod("firstCommand", CommandEvent.class), instance, adapters, validators).get());
        commandList.add(SlashCommandDefinition.build(instance.getClass().getDeclaredMethod("secondCommand", CommandEvent.class), instance, adapters, validators).get());
        commandList.add(SlashCommandDefinition.build(instance.getClass().getDeclaredMethod("thirdCommand", CommandEvent.class), instance, adapters, validators).get());

    }

    @Test
    public void getByName_WithTwoCommands_ShouldReturnList() {
        List<SlashCommandDefinition> commands = commandList.getByName("second name");

        assertEquals(2, commands.size());
        commands.forEach(command -> assertEquals("second name", command.getMetadata().getName()));
    }

    @Test
    public void getByCategory_WithTwoCommands_ShouldReturnList() {
        List<SlashCommandDefinition> commands = commandList.getByCategory("A");

        assertEquals(2, commands.size());
        commands.forEach(command -> assertEquals("A", command.getMetadata().getCategory()));
    }

    @Test
    public void getByLabel_WithOneCommand_ShouldReturnRightCommand() {
        SlashCommandDefinition command = commandList.getByLabel("first");

        assertTrue(command.getLabel().contains("first"));
    }

    @Test
    public void getSortedByCategories_WithTwoCategories_ShouldSort() {
        Map<String, List<SlashCommandDefinition>> sorted = commandList.getSortedByCategories();

        assertEquals(sorted.keySet(), new HashSet<String>() {{
            add("A");
            add("B");
        }});
        assertEquals(2, sorted.get("A").size());
        assertEquals(1, sorted.get("B").size());
    }
}
