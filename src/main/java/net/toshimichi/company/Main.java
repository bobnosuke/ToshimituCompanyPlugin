package net.toshimichi.company;

import net.milkbowl.vault.economy.Economy;
import net.toshimichi.company.command.AcceptCommand;
import net.toshimichi.company.command.AddItemCommand;
import net.toshimichi.company.command.BranchCommand;
import net.toshimichi.company.command.ConfirmCommand;
import net.toshimichi.company.command.CreateCommand;
import net.toshimichi.company.command.DeleteCommand;
import net.toshimichi.company.command.DepositCommand;
import net.toshimichi.company.command.JoinCommand;
import net.toshimichi.company.command.LeaderCommand;
import net.toshimichi.company.command.LeaveCommand;
import net.toshimichi.company.command.ListCommand;
import net.toshimichi.company.command.MenuCommand;
import net.toshimichi.company.command.PurgeCommand;
import net.toshimichi.company.command.RemoveItemCommand;
import net.toshimichi.company.command.RenameCommand;
import net.toshimichi.company.command.WithdrawCommand;
import net.toshimichi.company.data.CompanyRepository;
import net.toshimichi.company.service.CompanySyncService;
import net.toshimichi.company.service.CompanyUpdateService;
import net.toshimichi.company.service.ConfirmService;
import net.toshimichi.company.service.JoinRequestService;
import net.toshimichi.company.service.MenuService;
import net.toshimichi.company.service.ServiceRegistry;
import net.toshimichi.company.service.ShopIntegrationService;
import net.toshimichi.company.service.StoreService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends JavaPlugin {

    private ServiceRegistry serviceRegistry;

    @Override
    public void onEnable() {
        Path dataDir = getDataFolder().toPath();
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create data directory", e);
            }
        }

        // load config.yml
        Path configPath = dataDir.resolve("config.yml");
        YamlConfigurationLoader configLoader = YamlConfigurationLoader.builder()
                .path(configPath)
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        Config config;
        if (Files.exists(configPath)) {
            try {
                ConfigurationNode node = configLoader.load();
                config = node.get(Config.class);
            } catch (ConfigurateException e) {
                throw new RuntimeException("Could not load config.yml", e);
            }
        } else {
            try {
                config = new Config();
                ConfigurationNode node = configLoader.createNode();
                node.set(config);
                configLoader.save(node);
            } catch (ConfigurateException e) {
                throw new RuntimeException("Could not save config.yml", e);
            }
        }

        // load companies
        Path companiesDir = dataDir.resolve("companies");
        CompanyRepository companyRepository = new CompanyRepository(companiesDir);

        // register services
        CompanyUpdateService companyUpdateService = new CompanyUpdateService(this, config, companyRepository, dataDir.resolve("update.txt"));
        CompanySyncService companySyncService = new CompanySyncService(this, companyRepository);
        ConfirmService confirmService = new ConfirmService(this, config);
        JoinRequestService joinRequestService = new JoinRequestService(this, config, companyRepository);
        MenuService menuService = new MenuService(this);
        ShopIntegrationService shopIntegrationService = new ShopIntegrationService(this, companyRepository);
        StoreService storeService = new StoreService(dataDir.resolve("store.yml"));

        serviceRegistry = new ServiceRegistry(this);
        serviceRegistry.register(companyUpdateService);
        serviceRegistry.register(companySyncService);
        serviceRegistry.register(confirmService);
        serviceRegistry.register(joinRequestService);
        serviceRegistry.register(menuService);
        serviceRegistry.register(shopIntegrationService);
        serviceRegistry.register(storeService);
        serviceRegistry.onEnable();

        Economy economy = Bukkit.getServicesManager().load(Economy.class);
        if (economy == null) {
            throw new RuntimeException("Could not load Vault Economy");
        }

        // register commands
        BranchCommand branchCommand = new BranchCommand();
        branchCommand.addCommand("accept", new AcceptCommand(companyRepository, joinRequestService));
        branchCommand.addCommand("additem", new AddItemCommand(storeService));
        branchCommand.addCommand("confirm", new ConfirmCommand(confirmService));
        branchCommand.addCommand("create", new CreateCommand(config, economy, companyRepository));
        branchCommand.addCommand("delete", new DeleteCommand(companyRepository, confirmService, this));
        branchCommand.addCommand("deposit", new DepositCommand(companyRepository, economy));
        branchCommand.addCommand("join", new JoinCommand(companyRepository, joinRequestService));
        branchCommand.addCommand("leader", new LeaderCommand(companyRepository));
        branchCommand.addCommand("leave", new LeaveCommand(companyRepository));
        branchCommand.addCommand("list", new ListCommand(companyRepository, economy));
        branchCommand.addCommand("menu", new MenuCommand(config, economy, storeService, menuService, confirmService, companyUpdateService, companyRepository));
        branchCommand.addCommand("purge", new PurgeCommand(companyRepository, confirmService, this));
        branchCommand.addCommand("removeitem", new RemoveItemCommand(storeService));
        branchCommand.addCommand("rename", new RenameCommand(companyRepository));
        branchCommand.addCommand("withdraw", new WithdrawCommand(companyRepository, economy));

        getCommand("company").setExecutor(branchCommand);
        getCommand("company").setTabCompleter(branchCommand);
    }

    @Override
    public void onDisable() {
        serviceRegistry.onDisable();
    }
}
