package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.ControlImpl;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;

public interface Control extends Threshold {

    static Control of(ActionRowChildComponent component) {
        return new ControlImpl(component);
    }

    @Override
    Control threshold(int threshold);

    ActionRowChildComponent component();

}
