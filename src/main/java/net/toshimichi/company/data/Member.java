package net.toshimichi.company.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
final public class Member {

    private final UUID uniqueId;
    private final LocalDateTime joinedAt;

    private String name;
    private double deposit;
    private double sales;

    public static Member newMember(Player player) {
        return new Member(player.getUniqueId(), LocalDateTime.now(), player.getName(), 0, 0);
    }
}
