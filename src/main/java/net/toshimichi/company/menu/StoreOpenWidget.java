package net.toshimichi.company.menu;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.StorePurchaseLog;
import net.toshimichi.company.service.MenuService;
import net.toshimichi.company.service.StoreService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StoreOpenWidget implements Widget {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final Player player;
    private final StoreService storeService;
    private final MenuService menuService;
    private final Economy economy;
    private final Config config;
    private final CompanyRepository companyRepository;
    private final Company company;

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "ショップを開く");

        List<String> lore = new ArrayList<>();
        lore.add("");
        if (company.getStorePurchaseLogs().isEmpty()) {
            lore.add(ChatColor.WHITE + "直近の購入はありません");
        } else {
            lore.add(ChatColor.WHITE + "直近の購入");
            for (StorePurchaseLog log : company.getStorePurchaseLogs()) {
                lore.add(ChatColor.GOLD + log.getPlayer() + ChatColor.GRAY +
                        " - 費用: " + ChatColor.GOLD + economy.format(log.getPrice()) + ChatColor.GRAY +
                        " 購入日: " + ChatColor.GOLD + FORMATTER.format(log.getPurchasedAt().atOffset(ZoneOffset.ofHours(config.getTimezone()))));
            }
        }
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void onClick(ClickType type, InventoryAction action) {
        menuService.openMenu(player, new StoreMenu(player, storeService, economy, companyRepository, company));
    }
}
