package net.toshimichi.company.menu;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.StorePurchaseLog;
import net.toshimichi.company.service.StoreItem;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StoreItemWidget implements Widget {

    private static final int MAX_LOG_SIZE = 10;
    private final Player player;
    private final StoreItem storeItem;
    private final Economy economy;
    private final CompanyRepository companyRepository;
    private final Company company;

    @Override
    public ItemStack getItemStack() {
        ItemStack copy = new ItemStack(storeItem.getItemStack());
        ItemMeta meta = copy.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();
            else lore = new ArrayList<>(lore);

            if (company.getLevel() < storeItem.getLevel()) {
                lore.add("");
                lore.add(ChatColor.RED + "レベル " + storeItem.getLevel() + " で購入可能です (現在のレベル: " + company.getLevel() + ")");
            }

            lore.add(ChatColor.WHITE + "価格: " + ChatColor.GOLD + economy.format(storeItem.getPrice()));
            meta.setLore(lore);
        }
        copy.setItemMeta(meta);
        return copy;
    }

    @Override
    public void onClick(ClickType type, InventoryAction action) {
        if (company.getLevel() < storeItem.getLevel()) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "レベルが足りません");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
            return;
        }

        if (company.getBalance() < storeItem.getPrice()) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "お金が足りません");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
            return;
        }

        company.setBalance(company.getBalance() - storeItem.getPrice());

        // check if log can be merged
        boolean merged = false;
        root:
        if (!company.getStorePurchaseLogs().isEmpty()) {
            StorePurchaseLog log = company.getStorePurchaseLogs().get(0);
            if (!log.getPlayer().equals(player.getName())) break root;
            if (Duration.between(log.getPurchasedAt(), LocalDateTime.now(ZoneOffset.UTC)).toHours() > 1) break root;
            company.getStorePurchaseLogs().set(0, new StorePurchaseLog(player.getName(), log.getPrice() + storeItem.getPrice()));
            merged = true;
        }

        if (!merged) {
            company.getStorePurchaseLogs().add(0, new StorePurchaseLog(player.getName(), storeItem.getPrice()));
        }

        company.getStorePurchaseLogs().removeIf(log -> company.getStorePurchaseLogs().indexOf(log) >= MAX_LOG_SIZE);
        companyRepository.saveLater(company);

        player.getInventory().addItem(storeItem.getItemStack())
                .values().forEach(it -> player.getWorld().dropItem(player.getLocation(), it));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }
}
