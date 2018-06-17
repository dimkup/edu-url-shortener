package app.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * Retryer is a helper class which allows to retry some async action if it fails with a certain exception
 * @param <T>
 */
public class Retryer<T> {
    private final Supplier<CompletableFuture<T>> supplier;
    private CompletableFuture<T> outFuture;
    private Class exceptionClass;
    private int retry = 0;

    /**
     * Create the retryer
     * @param supplier - Async action to retry
     */
    public Retryer(Supplier<CompletableFuture<T>> supplier ) {
        this.supplier = supplier;
    }

    /**
     * Execute the action and retry it "times" times if it fails with "exceptionClass" exception
     * @param times - number of retries
     * @param excetpionClass - which exception will cause a retry
     * @return - CompletableFuture
     */
    public CompletableFuture<T> retryOn(int times,Class excetpionClass) {
        if (outFuture!=null) throw new IllegalStateException("Retryer instance is not reusable");
        retry = times;
        this.exceptionClass = excetpionClass;
        outFuture = new CompletableFuture();
        run();
        return outFuture;
    }

    /**
     * Run the async action
     */
    private void run() {
        supplier.get().thenAccept(this::accept).exceptionally(this::handel);
    }

    /**
     * Accept the result
     * @param res - result of the action
     */
    private void accept(T res) {
        outFuture.complete(res);
    }

    /**
     * handle the exception
     * @param ex - exception to handle
     * @return
     */

    private Void handel(Throwable ex) {
        Throwable t = ex instanceof CompletionException ?ex.getCause():ex;
        if (retry>0 && (exceptionClass==null||exceptionClass.isInstance(t))) {
            retry--;
            run();
        } else outFuture.completeExceptionally(ex);
        return null;
    }
}
