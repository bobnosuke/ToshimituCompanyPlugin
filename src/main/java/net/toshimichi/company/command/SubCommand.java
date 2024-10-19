package net.toshimichi.company.command;

import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    void onCommand(Player player, String label, String[] args);

    default List<String> onTabComplete(Player player, String label, String[] args) {
        return List.of();
    }

    String getPermission();

    String getDescription();
}
