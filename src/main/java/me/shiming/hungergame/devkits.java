package me.shiming.hungergame;

import org.bukkit.Material;

public class devkits {
    public static String $ (String str){
        return "§"+str;
    }

    public static Material ore_smelting (Material t){
        if (t == Material.RAW_COPPER){
            return Material.COPPER_INGOT;
        } else if (t == Material.RAW_IRON){
            return Material.IRON_INGOT;
        } else if (t == Material.RAW_GOLD){
            return Material.GOLD_INGOT;
        }
        return null;
    }
}
