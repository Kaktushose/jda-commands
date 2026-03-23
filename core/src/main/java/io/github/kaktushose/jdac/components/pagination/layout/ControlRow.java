package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.ControlRowImpl;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.SequencedCollection;

public non-sealed interface ControlRow extends PaginationLayout, Threshold {

    static ControlRow of(Control control, Control... controls) {
        return of(Helpers.mergeVararg(control, controls));
    }

    static ControlRow of(SequencedCollection<Control> controls) {
        return new ControlRowImpl(controls);
    }

    @Override
    ControlRow threshold(int threshold);

    SequencedCollection<Control> controls();
}
