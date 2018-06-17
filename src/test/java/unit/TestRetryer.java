package unit;

import app.util.Retryer;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class TestRetryer {
    @Test
    public void ShouldRetryNTimesOnSpecificException() {
        final AtomicInteger counter = new AtomicInteger();
        Retryer<String> retryer = new Retryer<>(()->CompletableFuture.supplyAsync(()->{
            if (counter.get()>3) return "123";
            counter.incrementAndGet();
            throw new RuntimeException("Test exception");
        }));
        Assert.assertEquals("123", retryer.retryOn(4,RuntimeException.class).join());
        Assert.assertEquals(4,counter.get());
    }

    @Test
    public void ShouldThrowExceptionIfNoMoreRetry() {
        final AtomicInteger counter = new AtomicInteger();
        Retryer<String> retryer = new Retryer<>(()->CompletableFuture.supplyAsync(()->{
            counter.incrementAndGet();
            throw new RuntimeException("jjdjasfui");
        }));
        try {
            retryer.retryOn(3,RuntimeException.class).join();
            Assert.fail("RuntimeException was not thrown");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().endsWith("jjdjasfui"));
        }
        Assert.assertEquals(4,counter.get());
    }

    @Test
    public void ShouldThrowImmediatelyIfOtherException() {
        final AtomicInteger counter = new AtomicInteger();
        Retryer<String> retryer = new Retryer<>(()->CompletableFuture.supplyAsync(()->{
            counter.incrementAndGet();
            throw new RuntimeException("jjdjasfui");
        }));
        try {
            retryer.retryOn(3,IllegalArgumentException.class).join();
            Assert.fail("RuntimeException was not thrown");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().endsWith("jjdjasfui"));
        }
        Assert.assertEquals(1,counter.get());
    }

    @Test
    public void ShouldThrowExceptionOnInstanceReuse() {
        Retryer<String> retryer = new Retryer<>(()->CompletableFuture.completedFuture("abc"));
        Assert.assertEquals("abc", retryer.retryOn(1,null).join());
        try {
            retryer.retryOn(1,null);
            Assert.fail("IllegalStateException was not thrown");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Retryer instance is not reusable",e.getMessage());
        }
    }
}
