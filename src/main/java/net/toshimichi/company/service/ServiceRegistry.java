package net.toshimichi.company.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServiceRegistry {

    private final Plugin plugin;
    private final List<Service> services = new ArrayList<>();
    private boolean enabled;
    private BukkitTask task;
    private int tick;

    public void register(Service service) {
        services.add(service);
        if (enabled) {
            service.onEnable();
        }
    }

    public void onEnable() {
        if (enabled) return;
        enabled = true;
        services.forEach(Service::onEnable);
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            tick++;
            services.forEach(service -> service.onTick(tick));
        }, 0, 1);
    }

    public void onDisable() {
        if (!enabled) return;
        enabled = false;
        services.forEach(Service::onDisable);
        task.cancel();
    }
}
