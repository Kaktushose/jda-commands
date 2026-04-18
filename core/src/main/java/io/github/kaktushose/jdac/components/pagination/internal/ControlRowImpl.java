package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Control;
import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.SequencedCollection;

@ApiStatus.Internal
public record ControlRowImpl(SequencedCollection<Control<? extends ActionRowChildComponent>> controls, int threshold) implements ControlRow {

    public ControlRowImpl {
        controls = Collections.unmodifiableSequencedCollection(controls);
    }

    public ControlRowImpl(SequencedCollection<Control<? extends ActionRowChildComponent>> controls) {
        this(controls, 1);
    }

    @Override
    public ControlRow threshold(int threshold) {
        return new ControlRowImpl(controls, threshold);
    }
}
