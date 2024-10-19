package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LeaveCommand implements SubCommand {

    private final CompanyRepository companyRepository;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業に所属していません");
            return;
        }

        if (company.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "社長は企業を解散してください");
            return;
        }

        company.getEmployees().removeIf(it -> it.getUniqueId().equals(player.getUniqueId()));
        companyRepository.saveLater(company);

        player.sendMessage(ChatColor.GOLD + "企業 " + ChatColor.YELLOW + company.getName() + ChatColor.GOLD + " から脱退しました");
        company.getOnlinePlayers().forEach(it -> it.sendMessage(ChatColor.GOLD + "企業 " + ChatColor.YELLOW + company.getName() + ChatColor.GOLD + " から " + ChatColor.YELLOW + player.getName() + ChatColor.GOLD + " が脱退しました"));
    }

    @Override
    public String getPermission() {
        return "company.leave";
    }

    @Override
    public String getDescription() {
        return "企業から脱退します";
    }
}
