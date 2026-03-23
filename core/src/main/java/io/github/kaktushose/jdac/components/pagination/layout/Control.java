package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.ControlImpl;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.ButtonComponent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.menu.StringSelectComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponentUnion;

public interface Control {

    static Control start(ButtonComponent control) {
        return new ControlImpl(ControlType.START, control);
    }

    static Control back(ButtonComponent control) {
        return new ControlImpl(ControlType.BACK, control);
    }

    static Control forth(ButtonComponent control) {
        return new ControlImpl(ControlType.FORTH, control);
    }

    static Control end(ButtonComponent control) {
        return new ControlImpl(ControlType.END, control);
    }

    static Control pageSelect(StringSelectComponent control) {
        return new ControlImpl(ControlType.PAGE_SELECT, control);
    }

    Control threshold(int threshold);

    enum ControlType {
        START,
        BACK,
        FORTH,
        END,
        PAGE_SELECT
    }
}
