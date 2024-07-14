package net.azisaba.mythicitemfinder;

import net.azisaba.lifepvelevel.util.Util;
import net.azisaba.mythicitemfinder.command.MythicItemFinderCommand;
import net.azisaba.mythicitemfinder.gui.ItemListScreen;
import net.azisaba.mythicitemfinder.sorter.GenericSorters;
import net.azisaba.mythicitemfinder.sorter.LifePvELevelSorter;
import net.azisaba.mythicitemfinder.sorter.SorterRegistry;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MythicItemFinder extends JavaPlugin {
    private final SorterRegistry sorterRegistry = new SorterRegistry();

    public static @NotNull MythicItemFinder getInstance() {
        return MythicItemFinder.getPlugin(MythicItemFinder.class);
    }

    @Override
    public void onEnable() {
        initSorterRegistry();
        Bukkit.getPluginManager().registerEvents(new ItemListScreen.EventListener(), this);
        Objects.requireNonNull(getCommand("mythicitemfinder")).setExecutor(new MythicItemFinderCommand());
    }

    private void initSorterRegistry() {
        sorterRegistry.put("NAME_A_TO_Z", GenericSorters.NAME_A_TO_Z);
        sorterRegistry.put("NAME_Z_TO_A", GenericSorters.NAME_Z_TO_A);
        sorterRegistry.put("MYTHIC_TYPE_A_TO_Z", GenericSorters.MYTHIC_TYPE_A_TO_Z);
        sorterRegistry.put("MYTHIC_TYPE_Z_TO_A", GenericSorters.MYTHIC_TYPE_Z_TO_A);
        if (Bukkit.getPluginManager().isPluginEnabled("LifePvELevel")) registerLifePvELevelSorter();
    }

    private void registerLifePvELevelSorter() {
        sorterRegistry.put("PVE_LEVEL_ASC", LifePvELevelSorter.LEVEL_ASC);
        sorterRegistry.put("PVE_LEVEL_DESC", LifePvELevelSorter.LEVEL_DESC);
    }

    public @NotNull SorterRegistry getSorterRegistry() {
        return sorterRegistry;
    }

    public static long getRequiredLevelIfEnabled(@NotNull ItemStack item) {
        if (Bukkit.getPluginManager().isPluginEnabled("LifePvELevel")) {
            return Util.getRequiredLevel(item);
        } else {
            return 1L;
        }
    }
}
