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

import net.azisaba.mythicitemfinder.MythicItemFinder;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class ItemStackUtil {
    private static final NamespacedKey MYTHIC_TYPE_KEY = new NamespacedKey(MythicItemFinder.getInstance(), "mythic_type");
    private static final NamespacedKey MYTHIC_FILE_KEY = new NamespacedKey(MythicItemFinder.getInstance(), "mythic_file");

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
        if (item == null || !item.hasItemMeta()) return "";
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(MYTHIC_TYPE_KEY, PersistentDataType.STRING, "");
    }

    public static @NotNull String getMythicFile(@Nullable ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(MYTHIC_FILE_KEY, PersistentDataType.STRING, "");
    }

    public static void markMythicItem(@NotNull ItemStack item, @NotNull String type, @NotNull String file) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.getPersistentDataContainer().set(MYTHIC_TYPE_KEY, PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(MYTHIC_FILE_KEY, PersistentDataType.STRING, file);
        item.setItemMeta(meta);
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
