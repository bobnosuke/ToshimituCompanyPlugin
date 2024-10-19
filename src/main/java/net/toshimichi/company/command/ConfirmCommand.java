package net.toshimichi.company.command;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.service.ConfirmService;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ConfirmCommand implements SubCommand {

    private final ConfirmService confirmService;

    @Override
    public void onCommand(Player player, String label, String[] args) {
        confirmService.acceptConfirmation(player);
    }

    @Override
    public String getPermission() {
        return "company.confirm";
    }

    @Override
    public String getDescription() {
        return "保留中のアクションを実行します";
    }
}
