package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.service.ConfirmService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class DeleteCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final ConfirmService confirmService;
    private final Plugin plugin;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Company company = companyRepository.findByMember(player.getUniqueId());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業に所属していません");
            return;
        }

        if (!company.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "企業のオーナーではありません");
            return;
        }

        confirmService.requireConfirmation(player, () -> {
            try {
                companyRepository.delete(company);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not delete company: " + company.getName() + " (" + company.getUniqueId() + ")", e);
                player.sendMessage(ChatColor.RED + "エラーが発生したため企業の削除に失敗しました");
            }
            player.sendMessage(ChatColor.GOLD + "企業を削除しました");
        });
    }

    @Override
    public String getPermission() {
        return "company.delete";
    }

    @Override
    public String getDescription() {
        return "企業を削除します";
    }
}
