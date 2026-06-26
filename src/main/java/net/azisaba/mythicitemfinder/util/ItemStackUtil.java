package net.azisaba.mythicitemfinder.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;

public class ItemStackUtil {
    public static @NotNull String getDisplayNameOrMaterialForSort(@NotNull ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            String name = item.getI18NDisplayName();
            if (name != null) {
                return name;
            }
            return item.getType().name();
        }
        return ChatColor.stripColor(item.getItemMeta().getDisplayName());
    }

    public static @NotNull String getMythicType(@Nullable ItemStack item) {
        if (item == null) return "";
        io.lumine.mythic.bukkit.adapters.BukkitItemStack mythicStack = new io.lumine.mythic.bukkit.adapters.BukkitItemStack(item);
        io.lumine.mythic.core.utils.jnbt.CompoundTag nbt = mythicStack.getNBT();
        if (nbt != null && nbt.containsKey("MYTHIC_TYPE")) {
            return nbt.getString("MYTHIC_TYPE");
        }
        return "";
    }

    public static @NotNull String getMythicFile(@Nullable ItemStack item) {
        String type = getMythicType(item);
        if (type.isEmpty()) return "";
        Optional<MythicItem> mythicItem = MythicBukkit.inst().getItemManager().getItem(type);
        return mythicItem.isPresent() ? mythicItem.get().getFile() : "";
    }

    public static @NotNull ItemStack createItem(@NotNull Material material, @NotNull String name) {
        return createItem(material, name, Collections.emptyList());
    }

    public static @NotNull ItemStack createItem(@NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
        meta.setDisplayName(name);
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
