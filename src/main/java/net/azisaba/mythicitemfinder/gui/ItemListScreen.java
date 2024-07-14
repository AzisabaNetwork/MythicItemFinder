package net.azisaba.mythicitemfinder.gui;

import net.azisaba.mythicitemfinder.MythicItemFinder;
import net.azisaba.mythicitemfinder.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemListScreen implements InventoryHolder {
    /**
     * the inventory
     */
    private final Inventory inventory = Bukkit.createInventory(this, 54, "アイテム一覧");

    /**
     * available sorters when instance was created
     */
    private final List<String> sorters = MythicItemFinder.getInstance().getSorterRegistry().keys().stream().sorted().collect(Collectors.toList());

    /**
     * items in this instance
     */
    private final List<ItemStack> items;

    /**
     * items in current page (0-53)
     */
    private final List<ItemStack> itemsInCurrentPage = new ArrayList<>(45);

    /**
     * Enable "exclude" button
     */
    private final boolean enableExcludeButton;

    private int sorter = 0;

    /**
     * current page
     */
    private int page = 0;

    /**
     * whether to show mythic type in lore or not
     */
    private boolean showMythicType = false;

    private boolean sorting = false;

    public ItemListScreen(@NotNull List<ItemStack> items, boolean enableExcludeButton) {
        this.items = new ArrayList<>(items);
        this.enableExcludeButton = enableExcludeButton;
        resetItems();
    }

    public void resetItems() {
        sorting = true;
        Bukkit.getScheduler().runTaskAsynchronously(MythicItemFinder.getInstance(), () -> {
            // sort items
            items.sort(MythicItemFinder.getInstance().getSorterRegistry().getOrThrow(sorters.get(sorter)));

            Bukkit.getScheduler().runTask(MythicItemFinder.getInstance(), () -> {
                sorting = false;
                inventory.clear();
                itemsInCurrentPage.clear();
                int fromIndex = page * 45;
                int toIndex = Math.min((page + 1) * 45, items.size());
                itemsInCurrentPage.addAll(items.subList(fromIndex, toIndex));
                for (int i = 0; i < itemsInCurrentPage.size(); i++) {
                    ItemStack item = itemsInCurrentPage.get(i);
                    if (showMythicType || enableExcludeButton) {
                        ItemStack stack = item.clone();
                        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
                        List<String> lore = meta.hasLore() ? Objects.requireNonNull(meta.getLore()) : new ArrayList<>();
                        String mythicType = ItemStackUtil.getMythicType(stack);
                        String mythicFile = ItemStackUtil.getStringTag(stack, "MYTHIC_FILE");
                        if (showMythicType) {
                            lore.add("");
                            lore.add("§7MMID: " + mythicType);
                            if (!mythicFile.isEmpty()) {
                                lore.add("§7Location: " + mythicFile);
                            }
                        }
                        if (enableExcludeButton) {
                            lore.add("");
                            if (MythicItemFinder.getRequiredLevelIfEnabled(stack) <= 0) {
                                lore.add("§cPvEレベルが0以下のため除外中");
                            }
                            if (MythicItemFinder.getInstance().getConfig().getStringList("excluded-mythic-types").contains(mythicType)) {
                                lore.add("§e右クリックでID除外解除");
                            } else {
                                lore.add("§e右クリックでID除外");
                            }
                            if (MythicItemFinder.getInstance().getConfig().getStringList("excluded-files").contains(mythicFile)) {
                                lore.add("§eShift+右クリックでファイル除外解除");
                            } else {
                                lore.add("§eShift+右クリックでファイル除外");
                            }
                        }
                        meta.setLore(lore);
                        stack.setItemMeta(meta);
                        inventory.setItem(i, stack);
                    } else {
                        inventory.setItem(i, item);
                    }
                }
                inventory.setItem(45, ItemStackUtil.createItem(Material.ARROW, (page == 0 ? "§7" : "§a") + "<< " + page));
                inventory.setItem(49, ItemStackUtil.createItem(Material.BARRIER, "§c閉じる"));
                if (showMythicType) {
                    inventory.setItem(50, ItemStackUtil.createItem(Material.LIME_DYE, "§cMMID表示をオフにする"));
                } else {
                    inventory.setItem(50, ItemStackUtil.createItem(Material.GRAY_DYE, "§aMMID表示をオンにする"));
                }
                List<String> sorterLore = sorters.stream().map(s -> " §f- " + (s.equals(sorters.get(sorter)) ? "§e" : "§7") + s).collect(Collectors.toList());
                inventory.setItem(51, ItemStackUtil.createItem(Material.REDSTONE_TORCH, "§a並び変え", sorterLore));
                inventory.setItem(53, ItemStackUtil.createItem(Material.ARROW, (page < getMaxPage() ? "§a" : "§7") + (page + 2) + " >>"));
            });
        });
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getMaxPage() {
        return (int) Math.floor(items.size() / 54.0);
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof ItemListScreen) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof ItemListScreen)) {
                return;
            }
            if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE || e.getClick() != ClickType.CREATIVE) {
                // allow creative (wheel) click but not otherwise
                e.setCancelled(true);
            }
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof ItemListScreen)) {
                return;
            }
            ItemListScreen screen = (ItemListScreen) e.getInventory().getHolder();
            if (e.getSlot() < 45) {
                if (screen.enableExcludeButton && e.isRightClick() && e.getSlot() < screen.itemsInCurrentPage.size()) {
                    if (e.isShiftClick() && e.getWhoClicked().hasPermission("mythicitemfinder.view-file")) {
                        String location = ItemStackUtil.getStringTag(screen.itemsInCurrentPage.get(e.getSlot()), "MYTHIC_FILE");
                        List<String> list = MythicItemFinder.getInstance().getConfig().getStringList("excluded-files");
                        if (!list.remove(location)) {
                            list.add(location);
                        }
                        MythicItemFinder.getInstance().getConfig().set("excluded-files", list);
                    } else {
                        String mythicType = ItemStackUtil.getMythicType(screen.itemsInCurrentPage.get(e.getSlot()));
                        List<String> list = MythicItemFinder.getInstance().getConfig().getStringList("excluded-mythic-types");
                        if (!list.remove(mythicType)) {
                            list.add(mythicType);
                        }
                        MythicItemFinder.getInstance().getConfig().set("excluded-mythic-types", list);
                    }
                    MythicItemFinder.getInstance().saveConfig();
                    screen.resetItems();
                }
                return;
            }
            if (e.getSlot() == 45 && screen.page > 0) {
                screen.page--;
            }
            if (e.getSlot() == 49) {
                e.getWhoClicked().closeInventory();
                return;
            }
            if (e.getSlot() == 50) {
                screen.showMythicType = !screen.showMythicType;
            }
            if (e.getSlot() == 51) {
                if (e.isRightClick()) {
                    screen.sorter--;
                    if (screen.sorter < 0) {
                        screen.sorter += screen.sorters.size();
                    }
                } else {
                    screen.sorter = (screen.sorter + 1) % screen.sorters.size();
                }
            }
            if (e.getSlot() == 53 && screen.page < screen.getMaxPage()) {
                screen.page++;
            }
            if (!screen.sorting) screen.resetItems();
        }
    }
}
