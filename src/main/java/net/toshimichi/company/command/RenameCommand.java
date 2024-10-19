package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RenameCommand implements SubCommand {

    private final CompanyRepository companyRepository;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業に所属していません");
            return;
        }

        if (!company.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "社長のみこのコマンドを実行できます");
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

        company.getOnlinePlayers().forEach(it -> it.sendMessage(ChatColor.GOLD + "企業 " + ChatColor.YELLOW + company.getName() + ChatColor.GOLD + " の名前が " + ChatColor.YELLOW + companyName + ChatColor.GOLD + " に変更されました"));
        company.setName(companyName);
        companyRepository.saveLater(company);
    }

    @Override
    public String getPermission() {
        return "company.rename";
    }

    @Override
    public String getDescription() {
        return "企業名を変更します";
    }
}
