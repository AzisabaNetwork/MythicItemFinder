package net.azisaba.mythicitemfinder.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Set;

public interface Registry<K, V> {
    @Nullable
    V get(@NotNull K k);

    @NotNull
    default V getOrThrow(@NotNull K k) throws NoSuchElementException {
        V v = get(k);
        if (v == null) {
            throw new NoSuchElementException();
        }
        return v;
    }

    @NotNull
    Set<K> keys();
}
