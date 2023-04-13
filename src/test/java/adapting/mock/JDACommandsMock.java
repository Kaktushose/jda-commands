package adapting.mock;

import com.github.kaktushose.jda.commands.JDACommands;

public class JDACommandsMock extends JDACommands {

    public JDACommandsMock() {
        super(jda, clazz, function, packages);
    }
}
