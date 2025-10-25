package definitions.description.environment;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;

import java.util.Optional;

@Interaction("base")
public class BaseClass {

    public void publicMethod() {
    }

    private void privateMethod() {
    }

    public BaserInnerClass returnType() {
        return new BaserInnerClass();
    }

    public String invoke(String input) {
        return input;
    }

    public void parameters(String first, int second, @Param String third, Optional<String> fourth) {

    }

    @Interaction
    public static class BaserInnerClass {

    }
}
