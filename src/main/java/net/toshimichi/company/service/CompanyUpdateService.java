package net.toshimichi.company.service;

import lombok.RequiredArgsConstructor;
import net.toshimichi.company.Config;
import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.logging.Level;

@RequiredArgsConstructor
public class CompanyUpdateService implements Service {

    private final Plugin plugin;
    private final Config config;
    private final CompanyRepository companyRepository;
    private final Path path;
    private int lastUpdateId;

    @Override
    public void onEnable() {
        if (Files.exists(path)) {
            try {
                lastUpdateId = Integer.parseInt(Files.readString(path));
            } catch (NumberFormatException | IOException e) {
                // use default (0)
            }
        }
    }

    private int getUniqueMonthId(LocalDateTime time) {
        return time.getYear() * 12 + time.getMonthValue();
    }

    public int getLevel(double sales) {
        int level = 0;
        for (Map.Entry<Integer, Double> entry : config.getLevels().entrySet()) {
            if (sales < entry.getValue()) continue;
            level = entry.getKey();
        }

        return level;
    }

    @Override
    public void onTick(int tick) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(config.getTimezone()));
        int id = getUniqueMonthId(now);
        if (lastUpdateId == id) return;

        lastUpdateId = id;
        try {
            Files.writeString(path, String.valueOf(id));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save last update id", e);
            return;
        }

        for (Company company : companyRepository.findAll()) {
            int prevLevel = company.getLevel();
            int level = getLevel(company.getSales());

            company.setLevel(level);
            company.setSales(0);
            companyRepository.saveLater(company);

            String message;
            if (level > prevLevel) {
                message = ChatColor.GOLD + "おめでとうございます! :)\n" +
                        ChatColor.YELLOW + "企業レベルが " + ChatColor.RED + prevLevel + ChatColor.YELLOW + " から " +
                        ChatColor.RED + level + ChatColor.YELLOW + " に上がりました!";
            } else {
                message = ChatColor.YELLOW + "企業レベルが " + ChatColor.RED + prevLevel + ChatColor.YELLOW + " から " +
                        ChatColor.RED + level + ChatColor.YELLOW + " になりました";
            }

            company.getOnlinePlayers().forEach(p -> p.sendMessage(message));
        }
    }
}
