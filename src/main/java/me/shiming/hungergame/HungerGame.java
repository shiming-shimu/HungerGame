package me.shiming.hungergame;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.shiming.hungergame.devkits.*;

public final class HungerGame extends JavaPlugin implements Listener {


    final String helloword = "3\n _   _                                ____                      \n" +
            "| | | |_   _ _ __   __ _  ___ _ __   / ___| __ _ _ __ ___   ___ \n" +
            "| |_| | | | | '_ \\ / _` |/ _ \\ '__| | |  _ / _` | '_ ` _ \\ / _ \\\n" +
            "|  _  | |_| | | | | (_| |  __/ |    | |_| | (_| | | | | | |  __/\n" +
            "|_| |_|\\__,_|_| |_|\\__, |\\___|_|     \\____|\\__,_|_| |_| |_|\\___|\n" +
            "                   |___/                                        ";


    YamlConfiguration config;

    Player op = null;
    public static String gameid = null;

    @Override
    public void onEnable() {
        // 注册
        getServer().getPluginManager().registerEvents(this, this);
        // 加载设置
        config = Config.getConfig(getDataFolder(), this);
        getLogger().info(config.getMapList("narrowing_rule.rule").toString());
        // 输出欢迎字符
        this.getLogger().info($(helloword));
    }

    @Override
    public void onDisable() {
        // 输出退出字符
        File file = new File(getDataFolder(),"config.yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getLogger().info($("4Hunger Game正在退出"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
        if( command.getName().equalsIgnoreCase("hgstart") ) {
            // 开始游戏指令
            gameid = UUID.randomUUID().toString().replace("-","");
            op = (Player) sender;
            try {
                Border.start(getLogger(), (Player)sender, config, this, gameid);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if( command.getName().equalsIgnoreCase("hgstop") ) {
            // 结束游戏指令
            Border.stop((Player)sender);
            return true;
        } else if( command.getName().equalsIgnoreCase("hgconfig") ) {
            // 设置游戏指令
            if (args.length < 1) {
                sender.sendMessage($("4使用错误 /hgconfig <规则名称> 规则在配置文件中设置！"));
                return false;
            }
            if (config.get(args[0]+".origin_length") == null) {
                sender.sendMessage($("4规则不存在 请在配置文件中设置！"));
                return false;
            }
            config.set("actived_rule", args[0]);
            sender.sendMessage($("2已将")+args[0]+"设置为活跃规则！");
            return true;
        }
        return false;
    }

    @EventHandler
    public void OnPlayerDeath (PlayerDeathEvent e) {
        if (op == null) return;
        // 关闭死亡不掉落
        e.setKeepInventory(false);

        // 设置死者为旁观者
        e.getEntity().setGameMode(GameMode.SPECTATOR);

        // 开始胜利检查
        int amt = 0;
        String winner = "";
        for (Player pl : op.getWorld().getPlayers()) {
            if (pl.getGameMode() == GameMode.SURVIVAL) {
                amt++;
                winner = pl.getName();
            }
        }
        if (amt == 1) {
            try {
                TimeUnit.SECONDS.sleep(1);
                op.performCommand("playsound minecraft:mob.enderdragon.growl voice @a");
                op.performCommand(String.format("title @a title {\"text\":\"%s 获胜！\", \"color\":\"red\"}", winner));
                TimeUnit.SECONDS.sleep(1);
                op.performCommand("playsound minecraft:mob.enderdragon.growl voice @a");
                op.performCommand(String.format("title @a title {\"text\":\"%s 获胜！\", \"color\":\"yellow\"}", winner));
                TimeUnit.SECONDS.sleep(1);
                op.performCommand("playsound minecraft:mob.enderdragon.growl voice @a");
                op.performCommand(String.format("title @a title {\"text\":\"%s 获胜！\", \"color\":\"blue\"}", winner));
                // 结束
                op.performCommand("gamemode spectator @a");
                op = null;
                gameid = null;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        } else if (amt < 1) {
            op.performCommand("title @a title {\"text\":\"错误：无人获胜\", \"color\":\"red\"}");
            // 结束
            op.performCommand("gamemode spectator @a");
            op = null;
            gameid = null;
        }
    }

    @EventHandler
    public void OnDrop (ItemSpawnEvent e){
        Material res = ore_smelting(e.getEntity().getItemStack().getType());
        if(res != null){
            ItemStack drop = e.getEntity().getItemStack();
            drop.setType(res);
            e.getEntity().getWorld().spawnParticle(Particle.LAVA, e.getLocation(), 8);
            e.getEntity().setItemStack(drop);
        }
    }
}
