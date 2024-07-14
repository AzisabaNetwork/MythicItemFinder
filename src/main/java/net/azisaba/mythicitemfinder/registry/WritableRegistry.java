package net.azisaba.mythicitemfinder.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WritableRegistry<K, V> implements Registry<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public V get(@NotNull K k) {
        Objects.requireNonNull(k, "k");
        return map.get(k);
    }

    @NotNull
    @Override
    public Set<K> keys() {
        return map.keySet();
    }

    public void put(@NotNull K k, @NotNull V v) {
        Objects.requireNonNull(k, "K");
        Objects.requireNonNull(v, "v");
        map.put(k, v);
    }
}
