package dependency;

import com.github.kaktushose.jda.commands.annotations.Produces;

public class WrongProducingClass {

    @Produces
    public Dependency getDependency(Object parameter) {
        return new Dependency();
    }

}
