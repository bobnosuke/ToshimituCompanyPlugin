package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.menu.TopMenu;
import net.toshimichi.company.service.CompanyUpdateService;
import net.toshimichi.company.service.ConfirmService;
import net.toshimichi.company.service.MenuService;
import net.toshimichi.company.service.StoreService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class MenuCommand implements SubCommand {

    private final Config config;
    private final Economy economy;
    private final StoreService storeService;
    private final MenuService menuService;
    private final ConfirmService confirmService;
    private final CompanyUpdateService companyUpdateService;
    private final CompanyRepository companyRepository;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業に所属していません");
            return;
        }

        menuService.openMenu(player, new TopMenu(config, economy, storeService, menuService, confirmService, companyUpdateService, companyRepository, company, player));
    }

    @Override
    public String getPermission() {
        return "company.menu";
    }

    @Override
    public String getDescription() {
        return "メニューを開きます";
    }
}
