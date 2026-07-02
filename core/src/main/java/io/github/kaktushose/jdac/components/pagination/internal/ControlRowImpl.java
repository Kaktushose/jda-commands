package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.layout.Control;
import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Predicate;

@ApiStatus.Internal
public record ControlRowImpl(
        SequencedCollection<Control<? extends ActionRowChildComponent>> controls,
        Predicate<Page> predicate,
        List<Entry> entries
) implements ControlRow {

    public ControlRowImpl {
        controls = Collections.unmodifiableSequencedCollection(controls);
    }

    public ControlRowImpl(SequencedCollection<Control<? extends ActionRowChildComponent>> controls) {
        this(controls, _ -> true, new ArrayList<>());
    }

    @Override
    public ControlRowImpl predicate(Predicate<Page> predicate) {
        return new ControlRowImpl(controls, predicate, entries);
    }

    @Override
    public ControlRowImpl entries(Collection<Entry> entries) {
        this.entries.addAll(entries);
        return this;
    }
}
