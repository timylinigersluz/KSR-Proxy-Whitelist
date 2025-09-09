package ch.ksrminecraft.kSRProxyWhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

@Plugin(
        id = "ksr-proxy-whitelist",
        name = "KSR-Proxy-Whitelist",
        version = "1.0.0",
        description = "Removes Velocity's connection prefix from whitelist kicks",
        authors = {"Timy Liniger"}
)
public class KSRProxyWhitelist {

    private final Logger logger;

    @Inject
    public KSRProxyWhitelist(Logger logger) {
        this.logger = logger;
    }

    @Subscribe
    public void onKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        // Original-Kickgrund vom Paper-Server
        Component reason = event.getServerKickReason().orElse(Component.empty());

        // Wenn der Grund "Whitelist" enthält → nur diese Nachricht anzeigen, ohne Velocity-Prefix
        String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(reason);
        if (plain.toLowerCase().contains("whitelist")) {
            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(reason));
            logger.info("KSR-Proxy-Whitelist: Prefix bei Kick für " + player.getUsername() + " entfernt.");
        }
    }
}
