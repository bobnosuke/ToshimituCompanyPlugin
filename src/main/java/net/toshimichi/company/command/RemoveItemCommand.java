package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.service.StoreService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RemoveItemCommand implements SubCommand {

    private final StoreService storeService;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "IDを入力してください");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
            if (id < 0 || id > 53) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "IDを入力してください");
            return;
        }

        storeService.removeItem(id);
        player.sendMessage(ChatColor.GOLD + "アイテムを削除しました");
    }

    @Override
    public String getPermission() {
        return "company.removeitem";
    }

    @Override
    public String getDescription() {
        return "アイテムを削除します";
    }
}
