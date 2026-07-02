package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.layout.Content;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
public record ContentImpl(
        Function<Page, ? extends SequencedCollection<ContainerChildComponent>> components,
        Predicate<Page> predicate,
        List<Entry> entries
) implements Content {

    public ContentImpl(Function<Page, ? extends SequencedCollection<ContainerChildComponent>> components) {
        this(components, _ -> true, new ArrayList<>());
    }

    @Override
    public PaginationLayout predicate(Predicate<Page> predicate) {
        return new ContentImpl(components, predicate, entries);
    }

    @Override
    public PaginationLayout entries(Collection<Entry> entries) {
        this.entries.addAll(entries);
        return this;
    }
}