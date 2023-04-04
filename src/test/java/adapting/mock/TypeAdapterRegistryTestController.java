package adapting.mock;

import adapting.CustomType;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Optional;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;

@CommandController
public class TypeAdapterRegistryTestController {

    public static final String OPTIONAL_DEFAULT = "default";

    @SlashCommand("1")
    public void stringArray(CommandEvent event, String[] args) {

    }

    @SlashCommand("2")
    public void inputLength(CommandEvent event, int i) {

    }

    @SlashCommand("3")
    public void optionalNull(CommandEvent event, @Optional int i) {

    }

    @SlashCommand("4")
    public void optionalDefault(CommandEvent event, @Optional(OPTIONAL_DEFAULT) String s) {

    }

    @SlashCommand("5")
    public void noAdapter(CommandEvent event, CustomType type) {

    }

    @SlashCommand("6")
    public void wrongArgument(CommandEvent event, int i) {

    }

}
