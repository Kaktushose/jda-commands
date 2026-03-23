package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Control;
import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;

import java.util.SequencedCollection;

public record ControlRowImpl(SequencedCollection<Control> controls, int threshold) implements ControlRow {

    public ControlRowImpl(SequencedCollection<Control> controls) {
        this(controls, 1);
    }

    @Override
    public ControlRow threshold(int threshold) {
        return new ControlRowImpl(controls, threshold);
    }
}
