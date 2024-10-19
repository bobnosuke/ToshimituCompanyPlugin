package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class ListCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final Economy economy;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        List<Company> companies = new ArrayList<>(companyRepository.findAll());
        companies.sort(Comparator.<Company>comparingInt(c -> c.getEmployees().size()).reversed());

        int index = 0;
        for (Company company : companies) {
            player.sendMessage(++index + ". " + ChatColor.GOLD + company.getName() + ChatColor.GRAY +
                    " - 従業員数: " + ChatColor.GOLD + company.getMembers().size() + ChatColor.GRAY + "人" +
                    " 資本金: " + ChatColor.GOLD + economy.format(company.getBalance()) + ChatColor.GRAY +
                    " レベル: " + ChatColor.GOLD + company.getLevel());
        }
    }

    @Override
    public String getPermission() {
        return "company.list";
    }

    @Override
    public String getDescription() {
        return "企業の一覧を表示します";
    }
}
