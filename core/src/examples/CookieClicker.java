import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

@Interaction
public class CookieClicker {

    private int count;

    @SlashCommand(value = "cookie clicker", desc = "Play cookie clicker")
    public void onClicker(CommandEvent event) {
        event.with().components("onCookie", "onReset").reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)
    public void onCookie(ComponentEvent event) {
        count++;
        event.reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Reset", emoji = "🔄", style = ButtonStyle.DANGER)
    public void onReset(ComponentEvent event) {
        count = 0;
        event.reply("You've got %s cookie(s)!", count);
    }

}

