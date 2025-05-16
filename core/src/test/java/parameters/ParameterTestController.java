package parameters;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.annotations.interactions.Optional;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class ParameterTestController {

    public void varArgs(Object... args) {

    }

    public void optional(@Optional Object argument) {

    }

    public void optionalWithDefault(@Optional("default") Object argument) {

    }

    public void constraintWithMessage(@Perm(value = "10", message = "error message") Member member) {

    }
}
