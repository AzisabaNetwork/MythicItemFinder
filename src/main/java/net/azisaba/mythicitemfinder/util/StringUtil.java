package net.azisaba.mythicitemfinder.util;

import org.jetbrains.annotations.NotNull;

public class StringUtil {
    public static @NotNull String getLastComponent(@NotNull String s, @NotNull String regex) {
        String[] array = s.split(regex);
        return array[array.length - 1];
    }
}
