package me.shiming.hungergame;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static me.shiming.hungergame.HungerGame.gameid;

public class Border {
    static void start(Logger l, Player p, YamlConfiguration config, Plugin plu, String gameid) throws InterruptedException {
        // 随机获取中心
        Random r = new Random(System.currentTimeMillis());
        int x = r.nextInt() % (20000000) * (r.nextInt() < 0 ? -1 : 1);
        int z = r.nextInt() % (20000000) * (r.nextInt() < 0 ? -1 : 1);

        // 提示
        p.performCommand("advancement revoke @a everything");
        p.performCommand("clear @a");
        p.performCommand("effect give @a minecraft:saturation 5 255 true");
        p.performCommand("gamemode spectator @a");
        p.performCommand("defaultgamemode spectator");
        p.performCommand("gamerule commandBlockOutput false");
        p.performCommand("worldborder warning time 30");
        p.performCommand(String.format("tellraw @a [{\"text\":\"已将战场设置在\",\"color\":\"blue\"},{\"text\":\" ( %d ~ %d ) \",\"color\":\"red\"},{\"text\":\"3秒后开始 请准备\",\"color\":\"blue\"}]", x, z));
        TimeUnit.SECONDS.sleep(1);
        p.performCommand("playsound minecraft:block.stone_button.click_off voice @a");
        p.performCommand("title @a title {\"text\":\"3\",\"color\":\"green\"} ");
        TimeUnit.SECONDS.sleep(1);
        p.performCommand("playsound minecraft:block.stone_button.click_off voice @a");
        p.performCommand("title @a title {\"text\":\"2\",\"color\":\"yellow\"} ");
        TimeUnit.SECONDS.sleep(1);
        p.performCommand("playsound minecraft:block.stone_button.click_off voice @a");
        p.performCommand("title @a title {\"text\":\"1\",\"color\":\"red\"} ");
        TimeUnit.SECONDS.sleep(1);
        // 传送
        p.performCommand("gamemode survival @a");
        p.performCommand(String.format("tp @a %d 300 %d", x, z));
        p.performCommand(String.format("spawnpoint @a %d 100 %d", x, z));
        p.performCommand("effect give @a minecraft:invisibility 40 1 true");
        p.performCommand("effect give @a minecraft:slow_falling 40 2 true");
        p.performCommand("effect give @a minecraft:speed 10 20 true");
        p.performCommand("playsound minecraft:entity.dragon_fireball.explode voice @a");

        // 边界设置
        p.performCommand(String.format("worldborder center %d %d", x, z));
        p.performCommand(String.format("worldborder set %d", config.getInt(config.getString("actived_rule")+".origin_length")));

        // 缩圈进程
        int i = 0, time = 0, length = 0;
        for (Map<?, ?> rule : config.getMapList(config.getString("actived_rule")+".rule")) {
            length = (Integer) rule.keySet().toArray()[0];
            time = (Integer) ((rule.values().toArray())[0]);
            if (i == 0) {
                p.performCommand(String.format("worldborder set %d %d", length, time));
                i += time;
            }
            else {
                new narrowing(length, time, p, gameid).runTaskLater(plu, i * 20L);
                i+= time;
            }
        }
    }
    static void stop(Player p) {
        // 结束
        p.performCommand("playsound minecraft:block.dispenser.fail voice @a");
        p.performCommand("title @a title {\"text\":\"错误：强制结束\", \"color\":\"red\"}");
        p.performCommand("gamemode spectator @a");
    }

}

class narrowing extends BukkitRunnable{
    int l, t;
    Player p;
    String g;
    public narrowing (int _l, int _t, Player _p, String gameid) {
        super();
        l = _l;
        t = _t;
        p = _p;
        g = gameid;
    }
    @Override
    public void run(){
        if ( g != gameid) this.cancel();
        else p.performCommand(String.format("worldborder set %d %d", l, t));
    }
}

