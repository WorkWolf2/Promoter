package com.minegolem.promoter.utils;

import com.minegolem.promoter.Promoter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class PlayerUtils {

    public static int getPlayerFivePercent(List<Player> playersOnline) {
        int playerCount = playersOnline.size();
        if (playerCount == 0) return 0;

        int effectiveFivePercent = (int) Math.floor((double) (playerCount * 5) / 100);

        if (effectiveFivePercent < 1) effectiveFivePercent = 1;

        return effectiveFivePercent;
    }
}
