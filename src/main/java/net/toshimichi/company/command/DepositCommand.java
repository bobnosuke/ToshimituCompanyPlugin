package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DepositCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final Economy economy;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業に所属していません");
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "金額を入力してください");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "金額を入力してください");
            return;
        }

        if (!economy.has(player, amount) || !economy.withdrawPlayer(player, amount).transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "所持金が足りません");
            return;
        }

        company.setBalance(company.getBalance() + amount);
        Member member = company.getMember(player.getUniqueId());
        member.setDeposit(member.getDeposit() + amount);
        companyRepository.saveLater(company);
        player.sendMessage(ChatColor.GOLD + "企業口座に " + ChatColor.YELLOW + amount + ChatColor.GOLD + " 円預けました");
    }

    @Override
    public String getPermission() {
        return "company.deposit";
    }

    @Override
    public String getDescription() {
        return "企業口座にお金を預けます";
    }
}
