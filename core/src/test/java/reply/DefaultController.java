package reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;

@Interaction
public class DefaultController {

    @Button
    public void defaultValues(ComponentEvent event) {

    }

    @Button
    @ReplyConfig(ephemeral = true, editReply = false, keepComponents = false)
    public void customValues(ComponentEvent event) {

    }
}
