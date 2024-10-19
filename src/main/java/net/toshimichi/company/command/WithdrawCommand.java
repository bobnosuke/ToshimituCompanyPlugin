package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WithdrawCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final Economy economy;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業に所属していません");
            return;
        }

        if (!company.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "企業の社長のみこのコマンドを実行できます");
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

        if (company.getBalance() < amount) {
            player.sendMessage(ChatColor.RED + "企業口座の残高が足りません");
            return;
        }

        if (!economy.depositPlayer(player, amount).transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "エラーが発生しました");
            return;
        }

        company.setBalance(company.getBalance() - amount);
        companyRepository.saveLater(company);
        player.sendMessage(ChatColor.GOLD + "企業口座から " + ChatColor.YELLOW + amount + ChatColor.GOLD + " 円引き出しました");
    }

    @Override
    public String getPermission() {
        return "company.withdraw";
    }

    @Override
    public String getDescription() {
        return "企業口座からお金を引き出します";
    }
}
