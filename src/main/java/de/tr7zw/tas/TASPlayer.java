package de.tr7zw.tas;

import com.mojang.realmsclient.gui.ChatFormatting;
import de.tr7zw.tas.duck.TASGuiContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class TASPlayer implements PlaybackMethod {

    public int step = 0;
    public boolean donePlaying = false;
    public boolean breaking = false;
    public GuiContainer gui;
    private Minecraft mc = Minecraft.getMinecraft();
    private List<KeyFrame> keyFrames;
    private int calcstate = 0;


    public TASPlayer(List<KeyFrame> keyFrames) {
        this.keyFrames = keyFrames;
    }

    public Float recalcYaw(float Yaw) {
        while (Yaw >= 180) Yaw -= 360;
        while (Yaw < -180) Yaw += 360;
        return Yaw;

    }

    private float uncalc(float yaw) {
        if (recalcYaw(mc.player.rotationYaw) >= 0 && (recalcYaw(mc.player.rotationYaw) - yaw) > 180) {
            calcstate++;
        }
        if (recalcYaw(mc.player.rotationYaw) < 0 && (recalcYaw(mc.player.rotationYaw) - yaw) < -180) {
            calcstate--;
        }
        return yaw + (360 * calcstate);
    }

    @Override
    public void updatePlayerMoveState() {                //When done playing, the game will pause...
        if (step >= keyFrames.size() - 1 || donePlaying) {
            if (!donePlaying) {
                donePlaying = true;
                mc.player.motionX = 0;
                mc.player.motionY = 0;
                mc.player.motionZ = 0;
                Minecraft.getMinecraft().displayGuiScreen(new GuiScreen() {
                });
                return;
            }
            return;
        }
        KeyFrame frame = keyFrames.get(step++);

        if (breaking) {
            step = keyFrames.size();
        }

        mc.gameSettings.keyBindAttack.pressed = (frame.leftClick.equalsIgnoreCase("pLK") || frame.leftClick.equalsIgnoreCase("hLK"));
        mc.gameSettings.keyBindUseItem.pressed = (frame.rightClick.equalsIgnoreCase("pRK") || frame.rightClick.equalsIgnoreCase("hRK"));
        mc.gameSettings.keyBindForward.pressed = frame.forwardKeyDown;
        mc.gameSettings.keyBindBack.pressed = frame.backKeyDown;
        mc.gameSettings.keyBindLeft.pressed = frame.leftKeyDown;
        mc.gameSettings.keyBindRight.pressed = frame.rightKeyDown;
        mc.gameSettings.keyBindJump.pressed = frame.jump;
        mc.gameSettings.keyBindSneak.pressed = frame.sneak;
        mc.gameSettings.keyBindSprint.pressed = frame.sprint;                //Read Sprint Key from File
        mc.player.inventory.currentItem = frame.slot;                    //Read Inventory Slot from File etc...
        mc.player.rotationPitch = frame.pitch;
        mc.player.rotationYaw = uncalc(frame.yaw);

        if ((frame.gui_clicked || frame.gui_typed || frame.gui_clickmoved || frame.gui_released) && (gui == null)) {
            TASUtils.sendMessage(ChatFormatting.RED + "Definitely desyncing (no GUI when one was expected), killing TAS now!");
            TAS.stopPlaying();
        }
        if (frame.gui_clicked) {
            try {
                TASUtils.sendMessage(String.format("(%d, %d) %d", frame.gui_mouseX, frame.gui_mouseY, frame.gui_mouseButton));
                Mouse.setCursorPosition(frame.gui_mouseX + gui.getGuiLeft(), frame.gui_mouseY + gui.getGuiTop());
                ((TASGuiContainer) gui).callMouseClicked(frame.gui_mouseX, frame.gui_mouseY, frame.gui_mouseButton);
            } catch (IOException e) {
                TASUtils.sendMessage(ChatFormatting.YELLOW + "Probably desyncing (GUI threw an error when clicking)");
            }
        } else if (frame.gui_typed) {
            try {
                ((TASGuiContainer) gui).callKeyPressed(frame.gui_typedChar, frame.gui_keyCode);
                TASUtils.sendMessage(String.format("%s %d", frame.gui_typedChar, frame.gui_keyCode));
            } catch (IOException e) {
                TASUtils.sendMessage(ChatFormatting.YELLOW + "Probably desyncing (GUI threw an error when typing)");
            }
        } else if (frame.gui_clickmoved) {
            ((TASGuiContainer) gui).callMouseClickMoved(frame.gui_mouseX, frame.gui_mouseY, frame.gui_mouseButton, frame.gui_timeSinceLastClick);
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            TASUtils.sendMessage(String.valueOf(scaledResolution.getScaleFactor()));
            Mouse.setCursorPosition(
                    (frame.gui_mouseX + gui.getGuiLeft()) * scaledResolution.getScaleFactor(),
                    (frame.gui_mouseY + gui.getGuiTop()) * scaledResolution.getScaleFactor()
            );
        } else if (frame.gui_released) {
            try {
                ((TASGuiContainer) gui).callMouseReleased(frame.gui_mouseX, frame.gui_mouseY, frame.gui_released_state);
            } catch (NullPointerException e) {
                TASUtils.sendMessage(ChatFormatting.YELLOW + "Probably desyncing (Release NPE?)");
            }
        }
    }
}