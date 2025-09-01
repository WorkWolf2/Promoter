package com.minegolem.promoter;

import com.minegolem.promoter.commands.AdminLinkCommand;
import com.minegolem.promoter.commands.RewardCommand;
import com.minegolem.promoter.data.manager.DataManager;
import com.minegolem.promoter.managers.CampaignManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;

@Getter
public final class Promoter extends JavaPlugin {

    public static CampaignManager campaignManager;
    @Getter
    public static DataManager dataManager;

    @Override
    public void onEnable() {
        dataManager = DataManager.of(this, "data.yml")
                .copyDefaultsFromJar(false)
                .build()
                .loadOrCreate();

        campaignManager = new CampaignManager(this);

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        getCommand("adminlink").setExecutor(new AdminLinkCommand());
        getCommand("premiotiktok").setExecutor(new RewardCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
