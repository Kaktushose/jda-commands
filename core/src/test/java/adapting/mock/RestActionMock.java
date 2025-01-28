package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@SuppressWarnings("DataFlowIssue")
public class RestActionMock<T> implements RestAction<T> {

    private final T member;

    public RestActionMock(T member) {
        this.member = member;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<T> setCheck(@Nullable BooleanSupplier booleanSupplier) {
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
}
