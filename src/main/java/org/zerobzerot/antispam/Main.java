package org.zerobzerot.antispam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Mod(
    modid = "clientside-antispam",
    name = "Clientside Antispam",
    version = "0.0.0-SNAPSHOT"
)
public class Main {

    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Config config = loadConfig();
    private final Set<String> bots = ConcurrentHashMap.newKeySet();

    public static Config loadConfig() {
        final File file = new File("antispam.json");
        try {
            return GSON.fromJson(new String(Files.readAllBytes(file.toPath())), Config.class);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        final Config defaults = new Config();
        try {
            FileUtils.writeStringToFile(file, GSON.toJson(defaults), Charset.defaultCharset());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return defaults;
    }

    public static Set<String> download() {
        System.out.println("Downloading blacklist from url: " + config.url.toString());
        final String response;
        try {
            final HttpsURLConnection connection = (HttpsURLConnection) config.url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            reader.close();
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
        System.out.println("Download finished.");
        return GSON.fromJson(response, new TypeToken<Set<String>>() {
        }.getType());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CompletableFuture
            .supplyAsync(Main::download)
            .thenAccept(bots::addAll);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.GAME_INFO) return;
        // some plugins are dumb and send whispers as system type message, so we have to check these too...
        final String[] parts = event.getMessage().getUnformattedText().split(" ");
        if (parts.length < 1) return;
        String name = StringUtils.stripControlCodes(parts[0])
            .toLowerCase()
            .replaceAll("<", "")
            .replaceAll(">", "")
            .replaceAll(":", "");
        if (bots.contains(name)) event.setCanceled(true);
    }

}
