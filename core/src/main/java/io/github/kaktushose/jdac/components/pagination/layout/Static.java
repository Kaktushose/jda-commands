package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.StaticImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.separator.Separator.Spacing;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.SequencedCollection;

public interface Static extends PaginationLayout {

    static Static divider(Spacing spacing) {
        return of(Separator.createDivider(spacing));
    }

    static Static spacing(Spacing spacing) {
        return of(Separator.createInvisible(spacing));
    }

    static Static text(String content) {
        return text(TextDisplay.of(content));
    }

    static Static text(TextDisplay textDisplay) {
        return of(textDisplay);
    }

    static Static of(ContainerChildComponent component, ContainerChildComponent... components) {
        return of(Helpers.mergeVararg(component, components));
    }

    static Static of(SequencedCollection<ContainerChildComponent> components) {
        return new StaticImpl(components);
    }
}
