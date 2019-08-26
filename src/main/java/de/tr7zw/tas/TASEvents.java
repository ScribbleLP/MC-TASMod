package de.tr7zw.tas;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Events for the TASmod
 *
 * @author ScribbleLP
 */
public class TASEvents {
    /**
     * Variable to enable fall damage<br>
     * If true, fall damage will be enabled
     */
    public static boolean FallDamage;
    public static boolean StopRecOnWorldClose;
    private Minecraft mc = Minecraft.getMinecraft();
    private String[] Buttons = null;


    //Cancel Fall Damage
    @SubscribeEvent
    public void onPlayerFalling(LivingFallEvent ev) {
        ev.setCanceled(!FallDamage);
    }

    //Message when joining the server
    @SubscribeEvent
    public void onOpenServer(PlayerEvent.PlayerLoggedInEvent ev) {
        TASUtils.sendMessage("TASmod enabled, type in /tasmod for more info");
    }

    //When hitting save and quit, the recording (with /record) stops
    @SubscribeEvent
    public void onCloseServer(PlayerEvent.PlayerLoggedOutEvent ev) {
        if (!TAS.doneRecording() && StopRecOnWorldClose) {
            TAS.stopRecording();
            if (!TAS.donePlaying()) {
                TAS.tasPlayer.breaking = true;
            }
        }
    }
}

