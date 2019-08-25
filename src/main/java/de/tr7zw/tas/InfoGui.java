package de.tr7zw.tas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * Gui-Overlay with useful information
 *
 * @author ScribbleLP
 */
public class InfoGui extends Gui {
    public static boolean Infoenabled;
    public static boolean Strokesenabled;
    private int line = 0;
    private Minecraft mc = Minecraft.getMinecraft();
    private String[] Buttons = null;

    public Float recalcYaw(float Yaw) {
        while (Yaw >= 180) Yaw -= 360;
        while (Yaw < -180) Yaw += 360;
        return Yaw;
    }

    @SubscribeEvent
    public void drawStuff(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || event.getType() != ElementType.HOTBAR) {
            return;
        }
        ScaledResolution scaled = new ScaledResolution(mc);
        int width = scaled.getScaledWidth();
        int height = scaled.getScaledHeight();
        if (!(mc.gameSettings.showDebugInfo)) {
            if (Infoenabled) {
                new Gui().drawCenteredString(mc.fontRenderer, (mc.player.posX - 0.5) + " " + Math.round((mc.player.posY)) + " " + (mc.player.posZ - 0.5), 130, 10, 0xFFFFFF);    //Coordinates
                new Gui().drawString(mc.fontRenderer, "Pitch: " + mc.player.rotationPitch, 16, 20, 0xFFFFFF);                //Show the current Pitch
                new Gui().drawString(mc.fontRenderer, "Yaw: " + recalcYaw(mc.player.rotationYaw), 22, 30, 0xFFFFFF);        //Show the current Yaw (This comes from the modversion for 1.7.10 since 1.7 has just SOUTH as a yaw in F3)

                new Gui().drawString(mc.fontRenderer, MouseInfo.getPointerInfo().getLocation().x + " " + MouseInfo.getPointerInfo().getLocation().y, 22, 40, 0xFFFFFF); //Current Pointer location
            }
            if (Strokesenabled) {
                drawKeyStrokes(height, width);
            }
            //Draw the Tickcounter. Value depends if playback or a recording is playing.
            if (!TAS.doneRecording()) {
                new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(TAS.recorder.recordstep + 1), 30, height - 24, 0xFFFFFF);
            } else if (TAS.tasPlayer != null && !TAS.tasPlayer.donePlaying) {
                new Gui().drawCenteredString(mc.fontRenderer, Integer.toString(TAS.tasPlayer.step + 1), 30, height - 24, 0xFFFFFF);
            }
        }
    }

    public void drawKeyStrokes(int height, int width) {
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "W", 3, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "A", 11, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "S", 19, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "D", 27, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "Space", 35, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "Shift", 67, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "Ctrl", 92, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "LK", 113, height - 13, 0xFFFFFF);
        }
        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
            new Gui().drawString(mc.fontRenderer, "RK", 127, height - 13, 0xFFFFFF);
        }
    }
}
