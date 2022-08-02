package org.learning.ratelimiter.core.concurrent;

import java.util.Optional;
import java.util.function.Supplier;

public interface Baton {
    void release();

    <T> Optional<T> get(Supplier<T> action);

    void doAction(Runnable action);

    boolean hasAcquired();
}
