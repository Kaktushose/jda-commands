package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.ControlRowImpl;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.SequencedCollection;

public interface ControlRow extends PaginationLayout {

    static ControlRow of(Control control, Control... controls) {
        return of(Helpers.mergeVararg(control, controls));
    }

    static ControlRow of(SequencedCollection<Control> controls) {
        return new ControlRowImpl(controls);
    }

    ControlRow threshold(int threshold);
}
