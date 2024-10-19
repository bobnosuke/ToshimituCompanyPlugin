package net.toshimichi.company.menu;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import net.toshimichi.company.service.ConfirmService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MemberWidget implements Widget {

    private final Player player;
    private final ConfirmService confirmService;
    private final CompanyRepository companyRepository;
    private final Economy economy;

    private final Config config;
    private final Company company;
    private final Member member;

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUniqueId()));

        String position = company.getLeader() == member ? ChatColor.RED + "社長" : ChatColor.AQUA + "社員";
        meta.setDisplayName(ChatColor.YELLOW + member.getName() + ChatColor.GRAY + " (" + position + ChatColor.GRAY + ")");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "合計預金額: " + economy.format(member.getDeposit()));
        lore.add(ChatColor.GRAY + "合計売上額: " + economy.format(member.getSales()));

        long memberDays = Duration.between(member.getJoinedAt(), LocalDateTime.now(ZoneOffset.UTC)).toDays();
        lore.add(ChatColor.GRAY + "メンバー日数: " + memberDays + "日");

        if (player.getUniqueId().equals(company.getLeader().getUniqueId()) && !player.getUniqueId().equals(member.getUniqueId())) {
            lore.add("");
            lore.add(ChatColor.RED + "クリックでこの社員を解雇する");
        }
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void onClick(ClickType type, InventoryAction action) {
        if (!player.getUniqueId().equals(company.getLeader().getUniqueId())) return;
        if (player.getUniqueId().equals(member.getUniqueId())) return;
        player.closeInventory();

        confirmService.requireConfirmation(player, () -> {
            company.getEmployees().remove(member);
            companyRepository.saveLater(company);
            player.sendMessage(ChatColor.GREEN + member.getName() + " を解雇しました");
        });
    }
}
