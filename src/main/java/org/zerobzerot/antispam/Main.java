package org.zerobzerot.antispam;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod(
    modid = "clientside-antispam",
    name = "Clientside Antispam",
    version = "0.0.0-SNAPSHOT"
)
public class Main {

    @Mod.Instance("clientside-antispam")
    public static Main INSTANCE;

    private final Set<String> bots = ConcurrentHashMap.newKeySet();

    private static Collection<String> downloadBotlist() {
        return new HashSet<>();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        bots.addAll(downloadBotlist());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatReceivedEvent event) {
        final String[] parts = event.getMessage().getUnformattedText().split(" ");
        if (parts.length < 2) return;
        // TODO: replace with regex matcher for vanilla chat format and some whisper plugin formats
        String first = parts[0].toLowerCase().replaceAll("<", "").replaceFirst(">", "");
        String second = parts[0].toLowerCase();
        if (bots.contains(first) || bots.contains(second)) event.setCanceled(true);
    }

}
