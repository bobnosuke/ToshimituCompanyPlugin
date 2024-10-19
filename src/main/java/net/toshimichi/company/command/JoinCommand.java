package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.service.JoinRequestService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class JoinCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final JoinRequestService joinRequestService;

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

        String companyName = String.join(" ", args);
        Company company = companyRepository.findByName(companyName);
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業が見つかりません");
            return;
        }

        joinRequestService.sendJoinRequest(player, company);
    }

    @Override
    public List<String> onTabComplete(Player player, String label, String[] args) {
        return CompanyTabCompleter.onTabComplete(companyRepository, args);
    }

    @Override
    public String getPermission() {
        return "company.join";
    }

    @Override
    public String getDescription() {
        return "企業に参加します";
    }
}
