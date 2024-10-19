package net.toshimichi.company.menu;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.service.StoreItem;
import net.toshimichi.company.service.StoreService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class StoreMenu implements Menu {

    private final Player player;
    private final StoreService storeService;
    private final Economy economy;
    private final CompanyRepository companyRepository;
    private final Company company;

    @Override
    public String getTitle() {
        return "ストア (" + ChatColor.DARK_BLUE + ChatColor.BOLD + company.getLevel() + " レベル" + ChatColor.RESET + ")";
    }

    @Override
    public Widget[] createWidgets() {
        Widget[] widgets = new Widget[54];
        for (Map.Entry<Integer, StoreItem> entry : storeService.getItems().entrySet()) {
            widgets[entry.getKey()] = new StoreItemWidget(player, entry.getValue(), economy, companyRepository, company);
        }

        return widgets;
    }
}
