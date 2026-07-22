package timesutils;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface Runners<T extends Runnable> {
	CompletableFuture<T> run(T task);
}
