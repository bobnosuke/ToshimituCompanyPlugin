package net.toshimichi.company.menu;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.data.Member;
import net.toshimichi.company.service.CompanyUpdateService;
import net.toshimichi.company.service.ConfirmService;
import net.toshimichi.company.service.MenuService;
import net.toshimichi.company.service.StoreService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class TopMenu implements Menu {

    private final Config config;
    private final Economy economy;
    private final StoreService storeService;
    private final MenuService menuService;
    private final ConfirmService confirmService;
    private final CompanyUpdateService companyUpdateService;
    private final CompanyRepository companyRepository;
    private final Company company;
    private final Player player;

    @Override
    public String getTitle() {
        return company.getName();
    }

    @Override
    public Widget[] createWidgets() {
        Widget[] widgets = new Widget[54];

        List<Member> members = new ArrayList<>(company.getMembers());
        members.sort(Comparator.comparing(Member::getJoinedAt));

        int index = 0;
        for (Member member : members) {
            widgets[index++] = new MemberWidget(player, confirmService, companyRepository, economy, config, company, member);
        }

        widgets[52] = new CompanyWidget(config, companyUpdateService, economy, company);
        widgets[53] = new StoreOpenWidget(player, storeService, menuService, economy, config, companyRepository, company);

        return widgets;
    }
}
