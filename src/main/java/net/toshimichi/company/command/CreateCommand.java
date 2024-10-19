package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CreateCommand implements SubCommand {

    private final Config config;
    private final Economy economy;
    private final CompanyRepository companyRepository;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company from = companyRepository.findByMember(player.getUniqueId());
        if (from != null) {
            player.sendMessage(ChatColor.RED + "すでに企業に所属しています");
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "企業名を入力してください");
            return;
        }

        String companyName = ChatColor.stripColor(String.join(" ", args));
        if (companyName.length() > 32) {
            player.sendMessage(ChatColor.RED + "企業名は32文字以内で入力してください");
            return;
        }

        if (companyRepository.findByName(companyName) != null) {
            player.sendMessage(ChatColor.RED + "その企業名はすでに存在します");
            return;
        }

        if (!economy.has(player, config.getCompanyCreateCost()) || !economy.withdrawPlayer(player, config.getCompanyCreateCost()).transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "企業を作成するには " + economy.format(config.getCompanyCreateCost()) + " 必要です");
            return;
        }

        Company company = Company.newCompany(companyName, Member.newMember(player));
        companyRepository.saveLater(company);
        player.sendMessage(ChatColor.GOLD + "企業 " + ChatColor.YELLOW + companyName + ChatColor.GOLD + " を作成しました");
    }

    @Override
    public String getPermission() {
        return "company.create";
    }

    @Override
    public String getDescription() {
        return "企業を作成します";
    }
}
