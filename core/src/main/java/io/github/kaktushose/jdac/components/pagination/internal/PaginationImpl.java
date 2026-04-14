package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.layout.*;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SequencedCollection;

import static io.github.kaktushose.jdac.components.pagination.layout.Control.Direction.BACKWARD;
import static io.github.kaktushose.jdac.components.pagination.layout.Control.Direction.FORWARD;

public final class PaginationImpl implements Pagination {

    private final SequencedCollection<PaginationLayout> paginationLayouts;
    private int currentPage;
    private @Nullable Integer maxPages;
    private ContainerConfig config;

    public PaginationImpl(
            SequencedCollection<PaginationLayout> paginationLayouts,
            int currentPage,
            @Nullable Integer maxPages,
            ContainerConfig config
    ) {
        this.paginationLayouts = paginationLayouts;
        this.currentPage = currentPage;
        this.maxPages = maxPages;
        this.config = config;
    }

    public PaginationImpl(SequencedCollection<PaginationLayout> paginationLayouts) {
        this(paginationLayouts, 1, null, new ContainerConfig(true, null, false));
    }

    @Override
    public Pagination maxPages(int maxPages) {
        Checks.check(maxPages > 0, "The maximum amount of pages must be at least 1");
        this.maxPages = maxPages;
        return this;
    }

    @Override
    public Pagination container(boolean container) {
        this.config = new ContainerConfig(container, null, false);
        return this;
    }

    @Override
    public Pagination color(@Nullable Integer color) {
        this.config = new ContainerConfig(true, color, config().spoiler());
        return this;
    }

    @Override
    public Pagination spoiler(boolean spoiler) {
        this.config = new ContainerConfig(true, config.color(), spoiler);
        return this;
    }

    @Override
    public Pagination forward(int amount) {
        int newPage = currentPage + amount;
        if (maxPages != null) {
            Checks.check(newPage <= maxPages, "Cannot scroll beyond the max page limit");
        }
        currentPage = newPage;
        return this;
    }

    @Override
    public Pagination backward(int amount) {
        int newPage = currentPage - amount;
        Checks.check(newPage > 0, "Cannot scroll back beyond page 1");
        currentPage = newPage;
        return this;
    }

    @Override
    public Pagination page(int page) {
        Checks.check(page < 1, "Cannot jump to negative page");
        if (maxPages != null) {
            Checks.check(page <= maxPages, "Cannot jump to page beyond max page limit");
        }
        currentPage = page;
        return this;
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
                case Dynamic dynamic -> dynamic.function().apply(new Page(this, currentPage, maxPages));
                case ControlRow controlRow -> List.of(ActionRow.of(controlRow.controls().stream()
                        .filter(it -> {
                            if (maxPages == null) {
                                return true;
                            }
                            return it.threshold() <= maxPages;
                        })
                        .map(control -> {
                            var component = control.component();
                            if (component instanceof ActionComponent actionComponent) {
                                actionComponent = actionComponent.withDisabled(false);

                                int newPage = currentPage - control.amount();
                                if (control.direction() == BACKWARD && newPage < 1) {
                                    actionComponent = actionComponent.withDisabled(true);
                                }

                                newPage = currentPage + control.amount();
                                if (control.direction() == FORWARD && maxPages != null && newPage > maxPages) {
                                    actionComponent = actionComponent.withDisabled(true);
                                }

                                component = (ActionRowChildComponent) actionComponent;
                            }
                            return new ControlImpl(component, control.direction(), control.amount(), control.threshold());
                        })
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

    public SequencedCollection<PaginationLayout> paginationLayouts() {
        return paginationLayouts;
    }

    public int currentPage() {
        return currentPage;
    }

    public @Nullable Integer maxPages() {
        return maxPages;
    }

    public ContainerConfig config() {
        return config;
    }

    private record ContainerConfig(boolean active, @Nullable Integer color, boolean spoiler) { }
}
