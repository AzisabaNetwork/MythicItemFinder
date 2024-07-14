package net.azisaba.mythicitemfinder.sorter;

import net.azisaba.lifepvelevel.util.Util;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class LifePvELevelSorter {
    public static final Comparator<ItemStack> LEVEL_ASC = Comparator.comparing(Util::getRequiredLevel);
    public static final Comparator<ItemStack> LEVEL_DESC = LEVEL_ASC.reversed();
}
