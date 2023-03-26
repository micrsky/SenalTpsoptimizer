package com.senalmc.tpsoptimizer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class TPSOptimizer extends JavaPlugin implements Listener {

    private YamlConfiguration messagesConfig;

    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);

        // 加载提示信息配置文件
        loadMessagesConfig();

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
                        player.kickPlayer(messagesConfig.getString("kick-message"));
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    private void loadMessagesConfig() {
        // 创建或加载配置文件
        File configFile = new File(getDataFolder(), "messages.yml");

        if (!configFile.exists()) {
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 给新加入的玩家发送提示信息
        Player player = event.getPlayer();
        player.sendMessage(messagesConfig.getString("welcome-message"));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        // 如果是由于延迟过高被踢出，给玩家发送提示信息
        if (event.getReason().equals(messagesConfig.getString("kick-message"))) {
            event.getPlayer().sendMessage("您已被服务器踢出，原因是您的延迟过高。请重新连接以优化游戏体验。");
        }
    }
}
