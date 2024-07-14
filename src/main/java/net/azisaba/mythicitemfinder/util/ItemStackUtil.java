package net.azisaba.mythicitemfinder.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        return getStringTag(item, "MYTHIC_TYPE");
    }

    public static @NotNull String getStringTag(@Nullable ItemStack item, @NotNull String tagName) {
        if (item == null) return "";
        net.minecraft.server.v1_15_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms.getOrCreateTag().getString(tagName);
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

    public static @NotNull ItemStack fixStack(@NotNull ItemStack item) {
        ItemStack stack = CraftItemStack.asCraftMirror(CraftItemStack.asNMSCopy(item));
        stack.setItemMeta(stack.getItemMeta());
        return stack;
    }
}
