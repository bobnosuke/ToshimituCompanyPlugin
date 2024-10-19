package net.toshimichi.company.service;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class ShopIntegrationService implements Service, Listener {

    private final Plugin plugin;
    private final CompanyRepository companyRepository;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onTransaction(TransactionEvent e) {
        Company company = companyRepository.findByMember(e.getOwnerAccount().getUuid());
        if (company == null) return;

        Company client = companyRepository.findByMember(e.getClient().getUniqueId());
        if (company == client) return;

        Member member = company.getMember(e.getOwnerAccount().getUuid());
        double price = e.getExactPrice().doubleValue();
        member.setSales(member.getSales() + price);
        company.setSales(company.getSales() + price);
        companyRepository.saveLater(company);
    }
}
