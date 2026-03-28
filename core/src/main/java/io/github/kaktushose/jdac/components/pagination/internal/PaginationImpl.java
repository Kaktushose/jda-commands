package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.layout.*;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SequencedCollection;

public record PaginationImpl(
        SequencedCollection<PaginationLayout> paginationLayouts,
        int currentPage,
        @Nullable Integer maxPages,
        ContainerConfig config
) implements Pagination {

    public PaginationImpl(SequencedCollection<PaginationLayout> paginationLayouts) {
        this(paginationLayouts, 1, null, new ContainerConfig(true, null, false));
    }

    @Override
    public Pagination maxPages(int maxPages) {
        Checks.check(maxPages > 0, "The maximum amount of pages must be at least 1");
        return new PaginationImpl(paginationLayouts, currentPage, maxPages, config);
    }

    @Override
    public Pagination container(boolean container) {
        return new PaginationImpl(paginationLayouts, currentPage, maxPages, new ContainerConfig(container, null, false));
    }

    @Override
    public Pagination color(@Nullable Integer color) {
        return new PaginationImpl(paginationLayouts, currentPage, maxPages, new ContainerConfig(true, color, config().spoiler()));
    }

    @Override
    public Pagination spoiler(boolean spoiler) {
        return new PaginationImpl(paginationLayouts, currentPage, maxPages, new ContainerConfig(true, config.color(), spoiler));
    }

    @Override
    public SequencedCollection<MessageTopLevelComponent> build() {
        List<ContainerChildComponent> result = new ArrayList<>();

        for (PaginationLayout paginationLayout : paginationLayouts) {
            if (maxPages != null && paginationLayout instanceof Threshold threshold && threshold.threshold() > maxPages) {
                continue;
            }

            SequencedCollection<ContainerChildComponent> components = switch (paginationLayout) {
                case Static staticImpl -> staticImpl.components();
                case Dynamic dynamic -> dynamic.function().apply(currentPage, Objects.requireNonNullElse(maxPages, -1));
                case ControlRow controlRow -> List.of(ActionRow.of(controlRow.controls().stream()
                        .filter(it -> maxPages != null && it.threshold() <= maxPages)
                        .map(Control::component)
                        .toList()));
            };

            result.addAll(components);
        }

        if (config.active()) {
            return List.of(Container.of(result).withAccentColor(config.color()).withSpoiler(config.spoiler()));
        }
        return result.stream().map(MessageTopLevelComponent.class::cast).toList();
    }

    private record ContainerConfig(boolean active, @Nullable Integer color, boolean spoiler) { }
}
