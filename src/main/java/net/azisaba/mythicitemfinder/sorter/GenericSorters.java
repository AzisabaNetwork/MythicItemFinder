package net.azisaba.mythicitemfinder.sorter;

import net.azisaba.mythicitemfinder.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class GenericSorters {
    public static final Comparator<ItemStack> NAME_A_TO_Z = Comparator.comparing(ItemStackUtil::getDisplayNameOrMaterialForSort);
    public static final Comparator<ItemStack> NAME_Z_TO_A = NAME_A_TO_Z.reversed();
    public static final Comparator<ItemStack> MYTHIC_TYPE_A_TO_Z = Comparator.comparing(ItemStackUtil::getMythicType);
    public static final Comparator<ItemStack> MYTHIC_TYPE_Z_TO_A = MYTHIC_TYPE_A_TO_Z.reversed();
}
