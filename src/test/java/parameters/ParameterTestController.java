package parameters;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.interactions.Optional;

public class ParameterTestController {

    public void varArgs(Object... args) {

    }

    public void primitives(int i) {

    }

    public void optional(@Optional Object argument) {

    }

    public void optionalWithDefault(@Optional("default") Object argument) {

    }

    public void constraint(@Min(10) int i) {

    }

    public void constraintWithMessage(@Min(value = 10, message = "error message") int i) {

    }
}
