package dependency;

import com.github.kaktushose.jda.commands.annotations.Inject;

public class InjectableClass {

    @Inject
    private Dependency dependency;

    public Dependency getDependency() {
        return dependency;
    }
}
