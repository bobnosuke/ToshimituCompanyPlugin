package net.toshimichi.company.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
final public class Company {

    private final UUID uniqueId;
    private final List<Member> employees = new ArrayList<>();
    private final LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);
    private final List<StorePurchaseLog> storePurchaseLogs = new ArrayList<>();

    private String name;
    private Member leader;
    private int level;
    private double balance;
    private double sales;

    public static Company newCompany(String name, Member president) {
        return new Company(UUID.randomUUID(), name, president, 0, 0, 0);
    }

    public List<Member> getMembers() {
        List<Member> result = new ArrayList<>(employees.size() + 1);
        result.add(leader);
        result.addAll(employees);

        return result;
    }

    public Member getMember(UUID uniqueId) {
        return getMembers()
                .stream()
                .filter(it -> it.getUniqueId().equals(uniqueId))
                .findAny()
                .orElse(null);
    }

    public List<Player> getOnlinePlayers() {
        return getMembers()
                .stream()
                .map(Member::getUniqueId)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }
}
