package net.toshimichi.company.menu;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.service.CompanyUpdateService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class CompanyWidget implements Widget {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final Config config;
    private final CompanyUpdateService companyUpdateService;
    private final Economy economy;
    private final Company company;

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "企業の情報");
        meta.setLore(List.of(
                "",
                ChatColor.WHITE + "企業名: " + ChatColor.GOLD + company.getName(),
                ChatColor.WHITE + "資本金: " + ChatColor.GOLD + economy.format(company.getBalance()),
                ChatColor.WHITE + "レベル: " + ChatColor.GOLD + economy.format(company.getLevel()),
                ChatColor.WHITE + "今月の売上: " + ChatColor.GOLD + company.getSales() +
                        ChatColor.WHITE + " (" + companyUpdateService.getLevel(company.getSales()) + " レベル相当)",
                ChatColor.WHITE + "設立日: " + ChatColor.GOLD + FORMATTER.format(company.getCreatedAt().atOffset(ZoneOffset.ofHours(config.getTimezone())))
        ));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void onClick(ClickType type, InventoryAction action) {

    }
}
