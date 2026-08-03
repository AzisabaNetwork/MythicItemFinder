package net.azisaba.mythicitemfinder.command;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import net.azisaba.mythicitemfinder.MythicItemFinder;
import net.azisaba.mythicitemfinder.gui.ItemListScreen;
import net.azisaba.mythicitemfinder.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MythicItemFinderCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }
        List<String> excludedMythicTypes = MythicItemFinder.getInstance().getConfig().getStringList("excluded-mythic-types");
        List<String> excludedFiles = MythicItemFinder.getInstance().getConfig().getStringList("excluded-files");
        boolean viewExcluded = sender.hasPermission("mythicitemfinder.view-excluded");
        MythicItemFinder plugin = MythicItemFinder.getInstance();
        plugin.getItemCache().getOrLoad(this::generateItems).thenAccept(cachedItems -> {
            List<ItemStack> items = cachedItems.stream().filter(stack -> {
                if (viewExcluded) {
                    return true;
                }
                return !excludedMythicTypes.contains(ItemStackUtil.getMythicType(stack))
                        && !excludedFiles.contains(ItemStackUtil.getMythicFile(stack))
                        && MythicItemFinder.getRequiredLevelIfEnabled(stack) > 0;
            }).collect(Collectors.toList());
            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> ((Player) sender).openInventory(new ItemListScreen(items, viewExcluded).getInventory()));
        });
        return true;
    }

    private @NotNull List<ItemStack> generateItems() {
        ItemExecutor itemManager = MythicBukkit.inst().getItemManager();
        return itemManager.getItems().stream().map(item -> {
            try {
                ItemStack stack = itemManager.getItemStack(item.getInternalName());
                ItemStackUtil.markMythicItem(stack, item.getInternalName(), item.getFile().getName());
                return stack;
            } catch (Exception e) {
                MythicItemFinder.getInstance().getLogger().warning("Failed to generate MythicItem: " + item.getInternalName() + " (" + e.getMessage() + ")");
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
