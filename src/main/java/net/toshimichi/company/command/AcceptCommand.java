package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.service.JoinRequestService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class AcceptCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final JoinRequestService joinRequestService;

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
            player.sendMessage(ChatColor.RED + "プレイヤー名を入力してください");
            return;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
            return;
        }

        joinRequestService.acceptJoinRequest(player, target.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) return List.of();
        if (!company.getLeader().getUniqueId().equals(player.getUniqueId())) return List.of();

        List<String> requests = joinRequestService.getJoinRequests(company.getUniqueId()).stream().map(Bukkit::getPlayer).filter(Objects::nonNull).map(Player::getName).toList();

        if (args.length < 1) return requests;

        return requests.stream().filter(it -> it.startsWith(args[0])).toList();
    }

    @Override
    public String getPermission() {
        return "company.accept";
    }

    @Override
    public String getDescription() {
        return "参加申請を承認します";
    }
}
