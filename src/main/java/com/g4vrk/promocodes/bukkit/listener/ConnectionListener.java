package com.g4vrk.promocodes.bukkit.listener;

import com.g4vrk.promocodes.database.repository.impl.PromoRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

@RequiredArgsConstructor
public class ConnectionListener implements Listener {

    private final PromoRepository promoRepository;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
       final Player player = event.getPlayer();

        try {
            promoRepository.saveUserIsAbsent(player.getUniqueId());
        } catch (final SQLException ex) {
            throw new RuntimeException("An error occurred while saving player " + player.getName(), ex);
        }
    }


}
