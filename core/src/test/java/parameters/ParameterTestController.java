package parameters;

import com.github.kaktushose.jda.commands.annotations.interactions.Param;

public class ParameterTestController {

    public void varArgs(Object... args) {

    }

    public void optional(@Param(optional = true) Object argument) {

    }

    public void optionalWithDefault(@Param(optional = true) Object argument) {

    }
}
