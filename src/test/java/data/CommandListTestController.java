package data;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;

@CommandController
public class CommandListTestController {

    @Command(value = "first", name = "first name", category = "A")
    public void firstCommand(CommandEvent event) {
    }

    @Command(value = "second", name = "second name", category = "A")
    public void secondCommand(CommandEvent event) {
    }

    @Command(value = "third", name = "second name", category = "B")
    public void thirdCommand(CommandEvent event) {
    }

}
