package data;

import com.github.kaktushose.jda.commands.annotations.SlashCommand;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;

@CommandController
public class CommandListTestController {

    @SlashCommand(value = "first", name = "first name", category = "A")
    public void firstCommand(CommandEvent event) {
    }

    @SlashCommand(value = "second", name = "second name", category = "A")
    public void secondCommand(CommandEvent event) {
    }

    @SlashCommand(value = "third", name = "second name", category = "B")
    public void thirdCommand(CommandEvent event) {
    }

}
