package com.snealmc.tpsoptimizer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSOptimizer extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);

        // 每1秒检查一次玩家的延迟并踢出超时玩家
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOnline() && player.hasPermission("tpsoptimizer.bypass")) {
                        // 不踢出有bypass权限的玩家
                        continue;
                    }

                    int latency = player.spigot().getPing();

                    if (latency > 300) {
                        player.kickPlayer("您的延迟过高，请重新连接以优化游戏体验。");
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 给新加入的玩家发送提示信息
        Player player = event.getPlayer();
        player.sendMessage("欢迎加入服务器！请保持您的网络畅通以获得最佳游戏体验。");
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        // 如果是由于延迟过高被踢出，给玩家发送提示信息
        if (event.getReason().equals("您的延迟过高，请重新连接以优化游戏体验。")) {
            event.getPlayer().sendMessage("您已被服务器踢出，原因是您的延迟过高。请重新连接以优化游戏体验。");
        }
    }
}
