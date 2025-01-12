import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;

@Interaction
public class PremiumCommand {

    @SlashCommand(value = "paid feature", scope = CommandScope.GUILD)
    public void onCommand(CommandEvent event) {
        event.reply("Hello World!");
    }

}