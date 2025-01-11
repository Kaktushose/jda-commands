package dependency;

import com.github.kaktushose.jda.commands.annotations.Produces;

public class ProducingClass {

    @Produces
    public Dependency getDependency() {
        return new Dependency();
    }

}
