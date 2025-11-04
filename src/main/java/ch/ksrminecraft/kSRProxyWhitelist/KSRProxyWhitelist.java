package ch.ksrminecraft.kSRProxyWhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.slf4j.Logger;

/**
 * ----------------------------------------------------------------------------
 *  🧩 KSR-Proxy-Whitelist
 *  ----------------------
 *  Velocity-Plugin, das den Proxy-Präfix bei Whitelist-Kicks entfernt.
 *
 *  Hintergrund:
 *   Wenn ein Spieler vom Paper-Server wegen "Whitelist" gekickt wird,
 *   fügt Velocity normalerweise den Präfix hinzu:
 *     ➜ "Verbindung zum Server fehlgeschlagen: <Grund>"
 *
 *  Dieses Plugin entfernt den Proxy-Präfix, damit die originale Nachricht
 *  des Paper-Servers (z. B. „Du bist nicht auf unserer Whitelist“) direkt
 *  angezeigt wird.
 *
 *  Autor: Timy Liniger
 *  Projekt: KSR Minecraft – Proxy Whitelist Filter
 * ----------------------------------------------------------------------------
 */
@Plugin(
        id = "ksr-proxy-whitelist",
        name = "KSR-Proxy-Whitelist",
        version = "1.0.1",
        description = "Removes Velocity's connection prefix from whitelist kicks.",
        authors = {"Timy Liniger"}
)
public class KSRProxyWhitelist {

    /** Logger für Proxy-Konsole (wird automatisch von Velocity injiziert). */
    private final Logger logger;

    /** Optionaler Debug-Schalter (setze auf true, um Logausgaben zu aktivieren). */
    private static final boolean DEBUG = false;

    @Inject
    public KSRProxyWhitelist(Logger logger) {
        this.logger = logger;
    }

    /**
     * Wird aufgerufen, wenn ein Spieler vom Backend-Server (Paper/Spigot)
     * gekickt wird. Hier greifen wir nur dann ein, wenn der Grund
     * offensichtlich mit "Whitelist" zu tun hat.
     *
     * @param event Kick-Ereignis von Velocity
     */
    @Subscribe(order = PostOrder.LAST)
    public void onKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        // Kickgrund (Component) vom Backend abrufen
        Component reason = event.getServerKickReason().orElse(Component.empty());

        // Text in reinen String umwandeln
        String plain = PlainTextComponentSerializer.plainText().serialize(reason);

        // Schutz gegen Null oder leeren Text
        if (plain == null || plain.isEmpty()) return;

        // Prüfen, ob es sich um eine Whitelist-Meldung handelt
        String lower = plain.toLowerCase();
        if (lower.contains("whitelist") || lower.contains("nicht auf unserer whitelist")) {

            // Spieler direkt mit Original-Nachricht disconnecten (ohne Proxy-Präfix)
            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(reason));

            if (DEBUG) {
                logger.info("[KSR-Proxy-Whitelist] Prefix bei Kick für {} entfernt. Grund: {}",
                        player.getUsername(), plain);
            }
        }
    }
}
