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

    private final T member;

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
    public CacheRestAction<T> setCheck(@Nullable BooleanSupplier booleanSupplier) {
        return null;
    }

    @Override
    public void queue(@Nullable Consumer<? super T> consumer, @Nullable Consumer<? super Throwable> consumer1) {

    }

    @Override
    public T complete(boolean b) throws RateLimitedException {
        return member;
    }

    @NotNull
    @Override
    public CompletableFuture<T> submit(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public CacheRestAction<T> useCache(boolean b) {
        return new CacheRestActionMock<>(member);
    }
}
