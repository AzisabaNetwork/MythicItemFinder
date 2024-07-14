package net.azisaba.mythicitemfinder.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Set;

public abstract class DelegateRegistry<K, V> implements Registry<K, V> {
    protected abstract @NotNull Registry<K, V> delegate();

    @Nullable
    @Override
    public V get(@NotNull K k) {
        return delegate().get(k);
    }

    @NotNull
    @Override
    public V getOrThrow(@NotNull K k) throws NoSuchElementException {
        return delegate().getOrThrow(k);
    }

    @NotNull
    @Override
    public Set<K> keys() {
        return delegate().keys();
    }
}
