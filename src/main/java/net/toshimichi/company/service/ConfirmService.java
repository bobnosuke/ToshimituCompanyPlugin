package net.toshimichi.company.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.toshimichi.company.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ConfirmService implements Service, Listener {

    private final Plugin plugin;
    private final Config config;

    private final List<Confirmation> confirmations = new ArrayList<>();
    private int tick;

    public Confirmation getConfirmation(UUID uniqueId) {
        return confirmations.stream()
                .filter(c -> c.getUniqueId().equals(uniqueId))
                .findAny()
                .orElse(null);
    }

    public void requireConfirmation(Player player, Runnable runnable) {
        Confirmation old = getConfirmation(player.getUniqueId());
        if (old != null) confirmations.remove(old);

        player.sendMessage(ChatColor.YELLOW + "本当に実行しますか? 実行する場合は " + ChatColor.RED + "/company confirm" + ChatColor.YELLOW + " を実行してください");
        confirmations.add(new Confirmation(player.getUniqueId(), runnable, tick));
    }

    public void acceptConfirmation(Player player) {
        Confirmation confirmation = getConfirmation(player.getUniqueId());
        if (confirmation == null) {
            player.sendMessage(ChatColor.RED + "保留中のアクションはありません");
            return;
        }

        confirmation.execute();
        confirmations.remove(confirmation);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void onTick(int tick) {
        this.tick = tick;

        confirmations.removeIf(confirmation -> tick - confirmation.getCreatedAt() > config.getConfirmTimeout());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Confirmation confirmation = getConfirmation(e.getPlayer().getUniqueId());
        if (confirmation != null) confirmations.remove(confirmation);
    }

    @Data
    private static class Confirmation {

        private final UUID uniqueId;
        private final Runnable runnable;
        private final int createdAt;

        public void execute() {
            runnable.run();
        }
    }
}
