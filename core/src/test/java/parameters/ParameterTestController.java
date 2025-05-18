package parameters;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import net.dv8tion.jda.api.entities.Member;

public class ParameterTestController {

    public void varArgs(Object... args) {

    }

    public void optional(@Param(optional = true) Object argument) {

    }

    public void optionalWithDefault(@Param(optional = true) Object argument) {

    }

    public void constraintWithMessage(@Perm(value = "10", message = "error message") Member member) {

    }
}
