package net.toshimichi.company.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchCommand implements TabExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public void addCommand(String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    public void showHelp(String label, Player player) {
        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            String key = entry.getKey();
            SubCommand value = entry.getValue();
            player.sendMessage(ChatColor.GOLD + "/" + label + " " + key + ChatColor.GRAY + " - " + ChatColor.YELLOW + value.getDescription());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
            return true;
        }

        if (args.length == 0) {
            showHelp(label, player);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            showHelp(label, player);
            return true;
        }

        if (!player.hasPermission(subCommand.getPermission())) {
            player.sendMessage(ChatColor.RED + "権限がありません");
            return true;
        }

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        subCommand.onCommand(player, args[0], subArgs);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 0) {
            return List.copyOf(subCommands.keySet());
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            return subCommands.entrySet().stream()
                    .filter(it -> it.getKey().toLowerCase().startsWith(args[0]))
                    .filter(it -> player.hasPermission(it.getValue().getPermission()))
                    .map(Map.Entry::getKey)
                    .toList();
        }

        if (!player.hasPermission(subCommand.getPermission())) return List.of();
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        return subCommand.onTabComplete(player, args[0], subArgs);
    }
}
