package net.toshimichi.company.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class JoinRequestService implements Service, Listener {

    private final Plugin plugin;
    private final Config config;
    private final CompanyRepository companyRepository;
    private final List<JoinRequest> requests = new ArrayList<>();
    private int tick;

    public List<UUID> getJoinRequests(UUID company) {
        return requests.stream()
                .filter(r -> r.getCompany().equals(company))
                .map(JoinRequest::getPlayer)
                .toList();
    }

    public JoinRequest getJoinRequest(UUID uniqueId) {
        return requests.stream()
                .filter(r -> r.getPlayer().equals(uniqueId))
                .findAny()
                .orElse(null);
    }

    public void sendJoinRequest(Player player, Company company) {
        Player president = Bukkit.getPlayer(company.getLeader().getUniqueId());
        if (president == null) {
            player.sendMessage(ChatColor.RED + "企業の社長がオフラインです");
            return;
        }

        JoinRequest request = getJoinRequest(player.getUniqueId());
        if (request != null) {
            player.sendMessage(ChatColor.RED + "既に申請中です");
            return;
        }

        player.sendMessage(ChatColor.GOLD + company.getName() + ChatColor.YELLOW + " に参加を申請しました");
        president.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " が企業に参加を申請しました");
        president.sendMessage(ChatColor.GOLD + "/company accept " + player.getName() + ChatColor.YELLOW + " で参加を許可します");
        requests.add(new JoinRequest(player.getUniqueId(), company.getUniqueId(), tick));
    }

    public void acceptJoinRequest(Player player, UUID target) {
        JoinRequest request = getJoinRequest(target);
        if (request == null) {
            player.sendMessage(ChatColor.RED + "申請が見つかりません");
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(request.getPlayer());
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "申請者がオフラインです");
            return;
        }

        Company company = companyRepository.findByUniqueId(request.getCompany());
        if (company == null) {
            player.sendMessage(ChatColor.RED + "企業が見つかりません");
            return;
        }

        company.getEmployees().add(Member.newMember(targetPlayer));
        companyRepository.saveLater(company);

        targetPlayer.sendMessage(ChatColor.GOLD + "企業 " + ChatColor.YELLOW + company.getName() + ChatColor.GOLD + " に参加しました");
        company.getOnlinePlayers().forEach(it -> it.sendMessage(ChatColor.GOLD + targetPlayer.getName() + ChatColor.YELLOW + " が企業に参加しました"));
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void onTick(int tick) {
        this.tick = tick;

        requests.removeIf(it -> {
            if (tick - it.getTick() < config.getJoinRequestTimeout()) return false;
            Player player = Bukkit.getPlayer(it.getPlayer());
            if (player != null) player.sendMessage(ChatColor.RED + "保留中のアクションがタイムアウトしました");
            return true;
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        JoinRequest request = getJoinRequest(e.getPlayer().getUniqueId());
        if (request == null) return;
        requests.remove(request);
    }

    @Data
    private static class JoinRequest {

        private final UUID player;
        private final UUID company;
        private final int tick;
    }
}
