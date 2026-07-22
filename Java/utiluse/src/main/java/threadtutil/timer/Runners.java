package threadtutil.timer;

import java.util.concurrent.CompletableFuture;

public interface Runners {

	CompletableFuture run(Runnable runnable);
}
