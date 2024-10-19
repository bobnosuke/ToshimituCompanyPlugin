package net.toshimichi.company.service;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class CompanySyncService implements Service, Listener {

    private final Plugin plugin;
    private final CompanyRepository companyRepository;

    private void flushSaveQueue() {
        try {
            companyRepository.flushSaveQueue();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save company data", e);
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        try {
            companyRepository.loadAll();
        } catch (IOException e) {
            throw new RuntimeException("Could not load companies", e);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        flushSaveQueue();
    }

    @Override
    public void onTick(int tick) {
        flushSaveQueue();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Company company = companyRepository.findByMember(e.getPlayer().getUniqueId());
        if (company == null) return;

        Member member = company.getMember(e.getPlayer().getUniqueId());
        if (member == null) throw new RuntimeException();
        if (member.getName().equals(e.getPlayer().getName())) return;

        member.setName(e.getPlayer().getName());
        companyRepository.saveLater(company);
    }
}
