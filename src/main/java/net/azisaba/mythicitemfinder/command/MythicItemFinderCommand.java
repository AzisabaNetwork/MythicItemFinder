package net.azisaba.mythicitemfinder.command;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import net.azisaba.mythicitemfinder.MythicItemFinder;
import net.azisaba.mythicitemfinder.gui.ItemListScreen;
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
        Bukkit.getScheduler().runTaskAsynchronously(MythicItemFinder.getInstance(), () -> {
            ItemExecutor itemManager = MythicBukkit.inst().getItemManager();
            List<ItemStack> items = itemManager.getItems().stream().map(item -> {
                if (!viewExcluded) {
                    if (excludedMythicTypes.contains(item.getInternalName())) {
                        return null;
                    }
                    if (excludedFiles.contains(item.getFile())) {
                        return null;
                    }
                }
                ItemStack stack = itemManager.getItemStack(item.getInternalName());
                if (!viewExcluded && MythicItemFinder.getRequiredLevelIfEnabled(stack) <= 0) {
                    return null;
                }
                return stack;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            Bukkit.getScheduler().runTask(
                    MythicItemFinder.getInstance(),
                    () -> ((Player) sender).openInventory(new ItemListScreen(items, viewExcluded).getInventory()));
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
