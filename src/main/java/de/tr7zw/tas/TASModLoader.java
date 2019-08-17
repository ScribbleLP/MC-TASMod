package de.tr7zw.tas;

import de.tr7zw.tas.commands.*;
import de.tr7zw.tas.networking.TeleportMessage;
import de.tr7zw.tas.networking.TeleportMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

import org.apache.logging.log4j.Logger;


@Mod(modid = "tasmod", name = "Tool Asisted Speedrun Mod")

public class TASModLoader {
	public static Logger LOGGER;


    @Instance
    public static TASModLoader instance = new TASModLoader();

    
    public static SimpleNetworkWrapper NETWORK;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	LOGGER=event.getModLog();
        //Config File
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        InfoGui.Infoenabled = config.get("General", "GuiOverlayOnStart", true, "While true, the Gui-Overlay will be shown when joining the world").getBoolean();
        InfoGui.Strokesenabled = config.get("General", "GuiKeyStrokesONStart", true, "While true, the KeyStrokes will be shown when joining the world").getBoolean();
        TASEvents.StopRecOnWorldClose = config.get("General", "StopRecordOnCloseWorld", true, "While true, the running recording (with /record) will be saved when closing the world with save and quit").getBoolean();
        TASEvents.FallDamage = config.get("General", "Falldamage", true, "While true, fall damage is enabled on world startup").getBoolean();
        config.save();
        
        //Networking
        NETWORK= NetworkRegistry.INSTANCE.newSimpleChannel("tasmod");
        NETWORK.registerMessage(TeleportMessageHandler.class, TeleportMessage.class, 0, Side.SERVER);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TASEvents());
        MinecraftForge.EVENT_BUS.register(TAS.class);
        new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + "tasfiles").mkdir();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new Tasmodc());
        event.registerServerCommand(new Playc());
        event.registerServerCommand(new Recordc());
        event.registerServerCommand(new Failc());
        event.registerServerCommand(new TasTpc());
        MinecraftForge.EVENT_BUS.register(new InfoGui());
    }

}
