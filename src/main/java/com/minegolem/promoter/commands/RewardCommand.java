package com.minegolem.promoter.commands;

import com.minegolem.promoter.Promoter;
import com.minegolem.promoter.scraper.TikTokChecker;
import com.minegolem.promoter.utils.PlayerUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RewardCommand implements CommandExecutor {

    private final Promoter plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;
        if (args.length < 1) return true;
        if (args.length > 1) return true;

        Set<UUID> rewardedPlayers = Promoter.campaignManager.getAllRewarded();
        Set<UUID> receivedPlayers = Promoter.campaignManager.getAllReceived();

        if (rewardedPlayers.contains(player.getUniqueId())) {
            player.sendMessage("hai già riscattato il tuo premio!");
            return true;
        }

        String link = args[0]; // link mandato dal player /premiotiktok link

        List<String> links = new ArrayList<String>(Promoter.campaignManager.getLinks()); // lista dei link inviati dai player
        Collections.shuffle(links);

        String receivedLink = Promoter.campaignManager.getAdminLink(); // link admin
        String playerReceivedLink = Promoter.campaignManager.getPlayerReceivedLink(player.getUniqueId());

        if (!rewardedPlayers.isEmpty()) receivedLink = links.getFirst(); // se hanno già riscattato il premio manda nuovo link

        try {
            if (link.equals(playerReceivedLink)) {
                Component message = LegacyComponentSerializer.legacySection().deserialize("&4&lATTENZIONE &7| &f Il link che hai mandato è uguale a quello che hai ricevuto! Prova a mandare un nuovo link!");

                player.sendMessage(message);
            }

            String finalReceivedLink = TikTokChecker.resolveFinalUrl(receivedLink);
            String finalLink = TikTokChecker.resolveFinalUrl(link);

            String linkId = TikTokChecker.extractVideoId(finalLink);
            String receivedLinkId = TikTokChecker.extractVideoId(finalReceivedLink);

            if (linkId != null && !linkId.equals(receivedLinkId)) {

                Component message = LegacyComponentSerializer.legacySection().deserialize("&4&lATTENZIONE &7| &f Il link che hai mandato non corrisponde al video! Prova a mandare un nuovo link!");
                player.sendMessage(message);

                return true;
            }

            Promoter.campaignManager.addRewarded(player.getUniqueId(), player.getName());
            Promoter.campaignManager.addLink(link);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Objects.requireNonNull(plugin.getConfig().getString("rewardCommand")).replace("{player}",  player.getName()));

            List<Player> playersOnline = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !rewardedPlayers.contains(p.getUniqueId()) || !receivedPlayers.contains(p.getUniqueId()))
                    .collect(Collectors.toList());

            Collections.shuffle(playersOnline);

            for (int i = 0; i < PlayerUtils.getPlayerFivePercent(playersOnline); i++) {
                Promoter.campaignManager.setPlayerReceivedLink(playersOnline.get(i).getUniqueId(), link);

                Component message = LegacyComponentSerializer.legacySection().deserialize("""
                        &5&lTIKTOK &7| &f Hai ricevuto un link! Ecco cosa devi fare:
                        &7• Apri il link e copia un nuovo link
                        &7• Fai &f/premiotiktok <link> &7 con i link che hai copiato\
                        &7• Riscatta il tuo premio!
                        
                        """);

                ClickEvent clickEvent = ClickEvent.openUrl(links.getFirst());

                playersOnline.get(i).sendMessage(message.append(LegacyComponentSerializer.legacySection().deserialize("&5&lCLICCA QUI").clickEvent(clickEvent)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
