package adapting.mock;

import adapting.CustomType;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Optional;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;

@Interaction
public class TypeAdapterRegistryTestController {

    public static final String OPTIONAL_DEFAULT = "default";

    @Command("1")
    public void stringArray(CommandEvent event, String[] args) {

    }

    @Command("2")
    public void inputLength(CommandEvent event, int i) {

    }

    @Command("3")
    public void optionalNull(CommandEvent event, @Optional int i) {

    }

    @Command("4")
    public void optionalDefault(CommandEvent event, @Optional(OPTIONAL_DEFAULT) String s) {

    }

    @Command("5")
    public void noAdapter(CommandEvent event, CustomType type) {

    }

    @Command("6")
    public void wrongArgument(CommandEvent event, int i) {

    }

}
