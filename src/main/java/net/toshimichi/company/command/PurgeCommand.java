package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.service.ConfirmService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

@RequiredArgsConstructor
public class PurgeCommand implements SubCommand {

    private final CompanyRepository companyRepository;
    private final ConfirmService confirmService;
    private final Plugin plugin;

    @Override
    public void onCommand(Player player, String label, String[] args) {
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
    public List<String> onTabComplete(Player player, String label, String[] args) {
        return CompanyTabCompleter.onTabComplete(companyRepository, args);
    }

    @Override
    public String getPermission() {
        return "company.purge";
    }

    @Override
    public String getDescription() {
        return "企業を強制削除します";
    }
}
