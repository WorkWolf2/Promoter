package com.minegolem.promoter.commands;

import com.minegolem.promoter.Promoter;
import com.minegolem.promoter.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AdminLinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!sender.hasPermission("promoter.admin")) return true;
        if (args.length < 1) return true;
        if (args.length > 1) return true;

        String link = args[0];

        @SuppressWarnings("unchecked")
        List<Player> playersOnline = new java.util.ArrayList<>((List<Player>) Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.hasPermission("promoter.admin"))
                .toList());

        Promoter.campaignManager.reset();
        Collections.shuffle(playersOnline);

        int effectiveFivePercent = PlayerUtils.getPlayerFivePercent(playersOnline);

        for (int i = 0; i < effectiveFivePercent; i++) {
            Player player = playersOnline.get(i);

            Component message = LegacyComponentSerializer.legacySection().deserialize("""
                    &5&lTIKTOK &7| &f Hai ricevuto un link! Ecco cosa devi fare:
                    &7• Apri il link e copia un nuovo link
                    &7• Fai &f/premiotiktok <link> &7 con i link che hai copiato\
                    &7• Riscatta il tuo premio!
                    
                    """);

            ClickEvent clickEvent = ClickEvent.openUrl(link);


            player.sendMessage(
                    message.append(LegacyComponentSerializer.legacySection().deserialize("&5&lCLICCA QUI").clickEvent(clickEvent))
            );

            Promoter.campaignManager.addReceived(player.getUniqueId(), player.getName());
            Promoter.campaignManager.setAdminLink(link);
            Promoter.campaignManager.setPlayerReceivedLink(player.getUniqueId(), link);
        }

        return true;
    }
}
