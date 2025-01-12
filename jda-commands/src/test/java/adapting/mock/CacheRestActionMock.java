package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CacheRestActionMock<T> implements CacheRestAction<T> {

    private T member;

    public CacheRestActionMock(T member) {
        this.member = member;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public CacheRestAction<T> setCheck(@Nullable BooleanSupplier checks) {
        return null;
    }

    @Override
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {

    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException {
        return member;
    }

    @NotNull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue) {
        return null;
    }

    @NotNull
    @Override
    public CacheRestAction<T> useCache(boolean useCache) {
        return null;
    }
}
