package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.service.StoreService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class AddItemCommand implements SubCommand {

    private final StoreService storeService;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "登録したいアイテムを手に持ってください");
            return;
        }

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

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "レベルを入力してください");
            return;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
            if (level < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "レベルを入力してください");
            return;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "価格を入力してください");
            return;
        }

        int price;
        try {
            price = Integer.parseInt(args[2]);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "価格を入力してください");
            return;
        }

        storeService.addItem(id, itemStack, level, price);
        player.sendMessage(ChatColor.GOLD + "アイテムを追加しました");
    }

    @Override
    public String getPermission() {
        return "company.additem";
    }

    @Override
    public String getDescription() {
        return "アイテムを追加します";
    }
}
