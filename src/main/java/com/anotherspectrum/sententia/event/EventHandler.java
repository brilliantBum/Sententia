package com.anotherspectrum.sententia.event;

import com.anotherspectrum.sententia.Sententia;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandler implements Listener {

    public EventHandler(Sententia sententia) {
        sententia.getServer().getPluginManager().registerEvents(this, sententia);
    }

    @org.bukkit.event.EventHandler
    private void onJoin(PlayerJoinEvent event) {
        new JoinQuitListener().onJoin(event);
    }

}
