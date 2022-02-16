package me.shiming.hungergame;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config {
    // 获取设置
    static YamlConfiguration getConfig(File folder, JavaPlugin plugin) {
        // 打开设置文件
        File file = new File(folder,"config.yml");
        // 如 文件/目录 不存在 则 新建/保存 文件夹/默认设置
        if(!folder.exists()) folder.mkdir();
        if(!file.exists()) plugin.saveDefaultConfig();
        // 加载设置
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        // 返回
        return config;
    }
}
