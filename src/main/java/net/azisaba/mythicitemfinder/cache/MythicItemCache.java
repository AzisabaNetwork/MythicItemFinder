package net.azisaba.mythicitemfinder.cache;

import io.lumine.mythic.bukkit.events.MythicReloadCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/** Caches generated Mythic item stacks until MythicMobs finishes reloading. */
public final class MythicItemCache implements Listener {
    private final Plugin plugin;
    private final Object lock = new Object();
    private @Nullable List<ItemStack> items;
    private @Nullable CompletableFuture<List<ItemStack>> loading;
    private long version;

    public MythicItemCache(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull CompletableFuture<List<ItemStack>> getOrLoad(@NotNull Supplier<List<ItemStack>> loader) {
        synchronized (lock) {
            if (items != null) {
                return CompletableFuture.completedFuture(copy(items));
            }
            if (loading != null) {
                return loading.thenApply(MythicItemCache::copy);
            }

            CompletableFuture<List<ItemStack>> future = new CompletableFuture<>();
            loading = future;
            long loadingVersion = version;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    List<ItemStack> loadedItems = copy(loader.get());
                    synchronized (lock) {
                        if (version == loadingVersion) {
                            items = loadedItems;
                        }
                        if (loading == future) {
                            loading = null;
                        }
                    }
                    future.complete(copy(loadedItems));
                } catch (Throwable throwable) {
                    synchronized (lock) {
                        if (loading == future) {
                            loading = null;
                        }
                    }
                    future.completeExceptionally(throwable);
                }
            });
            return future;
        }
    }

    public void invalidate() {
        synchronized (lock) {
            version++;
            items = null;
            loading = null;
        }
    }

    @EventHandler
    public void onMythicReloadComplete(@NotNull MythicReloadCompleteEvent event) {
        invalidate();
    }

    private static @NotNull List<ItemStack> copy(@NotNull List<ItemStack> source) {
        List<ItemStack> copy = new ArrayList<>(source.size());
        for (ItemStack item : source) {
            copy.add(item.clone());
        }
        return Collections.unmodifiableList(copy);
    }
}
