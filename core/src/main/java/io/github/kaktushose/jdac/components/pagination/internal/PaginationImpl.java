package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.layout.*;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.menu.StringSelectComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SequencedCollection;

import static io.github.kaktushose.jdac.components.pagination.layout.Control.Direction.BACKWARD;
import static io.github.kaktushose.jdac.components.pagination.layout.Control.Direction.FORWARD;

@ApiStatus.Internal
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
        Checks.check(maxPages > 0, "The maximum number of pages must be at least 1");
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
        Checks.check(page > 0, "Cannot jump to negative page");
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
                case Dynamic dynamic -> dynamic.function().apply(new Page(this));
                case ControlRow controlRow -> List.of(ActionRow.of(controlRow.controls().stream()
                        .filter(it -> {
                            if (maxPages == null) {
                                return true;
                            }
                            return it.threshold() <= maxPages;
                        })
                        .map(control -> switch (control) {
                            case PageButton button -> {
                                int newPage = currentPage - button.amount();
                                if (button.direction() == BACKWARD && newPage < 1) {
                                    yield disable(button, true);
                                }
                                newPage = currentPage + button.amount();
                                if (button.direction() == FORWARD && maxPages != null && newPage > maxPages) {
                                    yield disable(button, true);
                                }
                                yield disable(button, false);
                            }
                            case PageSelect pageSelect -> {
                                // StringSelectComponent is a JDA-Commands class and doesn't support #createCopy().
                                // JDA also doesn't have a StringSelectMenu#withOptions method so this is the workaround
                                StringSelectMenu component = pageSelect.component();
                                if (component instanceof StringSelectComponent menu) {
                                    menu.getOptions().clear();
                                    yield pageSelect(menu.selectOptions(options(pageSelect)), pageSelect);
                                }
                                var copy = component.createCopy();
                                copy.getOptions().clear();
                                copy.addOptions(options(pageSelect));
                                yield pageSelect(copy.build(), pageSelect);
                            }
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

    public record ContainerConfig(boolean active, @Nullable Integer color, boolean spoiler) { }

    private PageButtonImpl disable(PageButton button, boolean disable) {
        return new PageButtonImpl(button.component().withDisabled(disable), button.direction(), button.amount(), button.threshold());
    }

    private PageSelectImpl pageSelect(StringSelectMenu menu, PageSelect pageSelect) {
        return new PageSelectImpl(menu, pageSelect.threshold(), pageSelect.selectOptions(), pageSelect.format());
    }

    private List<SelectOption> options(PageSelect pageSelect) {
        int options;
        if (maxPages == null) {
            // if max pages isn't set, set option count to user setting, else current page
            options = Objects.requireNonNullElse(pageSelect.selectOptions(), currentPage);
        } else if (pageSelect.selectOptions() != null) {
            // if max pages and user setting is present, set option count to user setting as long as it's smaller than max pages
            options = Math.min(pageSelect.selectOptions(), maxPages);
        } else {
            // else just set it to max pages
            options = maxPages;
        }
        List<SelectOption> result = new ArrayList<>();
        for (int i = 1; i <= Math.min(options, StringSelectMenu.OPTIONS_MAX_AMOUNT); i++) {
            result.add(SelectOption.of(pageSelect.format().formatted(i), String.valueOf(i)));
        }
        return result;
    }
}
