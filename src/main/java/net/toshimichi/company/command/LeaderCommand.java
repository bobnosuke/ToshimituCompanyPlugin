package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class LeaderCommand implements SubCommand {

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
            player.sendMessage(ChatColor.RED + "新しい社長の名前を指定してください");
            return;
        }

        Player newLeader = Bukkit.getPlayerExact(args[0]);
        if (newLeader == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
            return;
        }

        if (player == newLeader) {
            player.sendMessage(ChatColor.RED + "すでにあなたは社長です");
            return;
        }

        Member member = company.getMember(newLeader.getUniqueId());
        if (member == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーは企業に所属していません");
            return;
        }

        company.getEmployees().add(company.getLeader());
        company.getEmployees().remove(member);
        company.setLeader(member);
        companyRepository.saveLater(company);

        player.sendMessage(ChatColor.GOLD + "社長を " + ChatColor.YELLOW + newLeader.getName() + ChatColor.YELLOW + " に変更しました");
        newLeader.sendMessage(ChatColor.GOLD + "あなたは " + company.getName() + ChatColor.YELLOW + " の社長になりました");
    }

    @Override
    public List<String> onTabComplete(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) return List.of();

        return MemberTabCompleter.onTabComplete(company, args);
    }

    @Override
    public String getPermission() {
        return "company.leader";
    }

    @Override
    public String getDescription() {
        return "社長を変更します";
    }
}
