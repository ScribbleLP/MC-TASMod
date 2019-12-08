package de.tr7zw.tas;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;

import de.tr7zw.tas.duck.PlaybackInput;
import de.tr7zw.tas.duck.TASGuiContainer;
import me.guichaguri.tastickratechanger.TickrateChanger;
import me.guichaguri.tastickratechanger.api.TickrateAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;

public class TASPlayer implements PlaybackMethod {

    public int step = 0;
    public boolean donePlaying = false;
    public boolean breaking = false;
    public GuiScreen gui;
    private static Minecraft mc = Minecraft.getMinecraft();
    private List<KeyFrame> keyFrames;
    private static int calcstate = 0;
    private boolean openedInventory;
    private static int presstimeLK=0;
    private static int presstimeRK=0;
    public static KeyFrame pFrame;
    private static float betweenyaw;
    /**
     * Set's headrotations between Ticks to smoothen the playback
     */
    public static boolean fillerHeadrotations;

    public TASPlayer(List<KeyFrame> keyFrames) {
        TAS.playing_back = true;
        this.keyFrames = keyFrames;
        betweenyaw=uncalc(keyFrames.get(0).yaw, mc.player.rotationYaw);
    }

    public static void keyBindsHook() {
        updateKeybinds(pFrame, pFrame);
    }

    public static Float recalcYaw(float Yaw) {
        while (Yaw >= 180) Yaw -= 360;
        while (Yaw < -180) Yaw += 360;
        return Yaw;

    }

    private static float uncalc(float yaw, float currentyaw) {
        if (recalcYaw(currentyaw) >= 0 && (recalcYaw(currentyaw) - yaw) > 180) {
            calcstate++;
        }
        if (recalcYaw(currentyaw) < 0 && (recalcYaw(currentyaw) - yaw) < -180) {
            calcstate--;
        }
        return yaw + (360 * calcstate);
    }

    @Override
    public void updatePlayerMoveState() {                //When done playing, the game will pause...
        if (step >= keyFrames.size() - 1) {
            if (!donePlaying) {
                TAS.playing_back = false;
                donePlaying = true;
                if(!breaking) {
	                mc.player.motionX = 0;
	                mc.player.motionY = 0;
	                mc.player.motionZ = 0;
	                mc.player.velocityChanged=true;
	                Minecraft.getMinecraft().displayGuiScreen(new GuiScreen() {
	                });
                }else{
                	unpressAllKeys();
                }
                ((PlaybackInput) mc).setPlayback(null);
                return;
            }
            return;
        }
        KeyFrame frame = keyFrames.get(step++);
        KeyFrame framenext = keyFrames.get(step);
        if(keyFrames.size()!=step) {
        	
        }
        if (breaking) {
            step = keyFrames.size();
        }

        updateKeybinds(frame, framenext);


        openedInventory = frame.inventory;
        if (!frame.gui_states.isEmpty()) {
            if (gui == null) {
                //TASUtils.sendMessage(ChatFormatting.RED + "(Almost) Definitely desyncing (no GUI when one was expected)");
                return;
            }

            for (GuiFrame guiFrame : frame.gui_states) {
                switch (guiFrame.type) {
                    case GUI_DUMMY:
                        break;
                    case GUI_RELEASED:
                        try {
                            moveMouse(guiFrame.gui_mouseX, guiFrame.gui_mouseY);
                            ((TASGuiContainer) gui).callMouseReleased(guiFrame.gui_mouseX, guiFrame.gui_mouseY, guiFrame.gui_released_state);
                        } catch (NullPointerException e) {
                        	TASModLoader.LOGGER.info("Probably desyncing (Release NPE?)");
                        }
                        break;
                    case GUI_CLICKED:
                        try {
                            TASModLoader.LOGGER.info("("+guiFrame.gui_mouseX+", "+guiFrame.gui_mouseY+") "+guiFrame.gui_mouseButton);
                            moveMouse(guiFrame.gui_mouseX, guiFrame.gui_mouseY);
                            ((TASGuiContainer) gui).callMouseClicked(guiFrame.gui_mouseX, guiFrame.gui_mouseY, guiFrame.gui_mouseButton);
                        } catch (IOException e) {
                        	TASModLoader.LOGGER.info("Probably desyncing (GUI threw an error when clicking)");
                        }
                        break;
                    case GUI_MOUSE_DRAGGED:
                        moveMouse(guiFrame.gui_mouseX, guiFrame.gui_mouseY);
                        ((TASGuiContainer) gui).callMouseClickMoved(guiFrame.gui_mouseX, guiFrame.gui_mouseY, guiFrame.gui_mouseButton, guiFrame.gui_timeSinceLastClick);
                        break;
                    case GUI_TYPED:
                        try {
                            moveMouse(guiFrame.gui_mouseX, guiFrame.gui_mouseY);
                            ((TASGuiContainer) gui).callKeyPressed(guiFrame.gui_typedChar, guiFrame.gui_keyCode);
                            TASModLoader.LOGGER.info(guiFrame.gui_typedChar+" "+guiFrame.gui_keyCode);
                        } catch (IOException e) {
                        	TASModLoader.LOGGER.info("Probably desyncing (GUI threw an error when typing)");
                        }
                        break;
                }
            }
        }

        KeyFrame nextframe = keyFrames.get(step);

        if (nextframe != null) {
            if (!nextframe.gui_states.isEmpty() && nextframe.gui_states.get(0).type != GuiFrame.FrameType.GUI_DUMMY) {
                moveMouse(nextframe.gui_states.get(0).gui_mouseX, nextframe.gui_states.get(0).gui_mouseY);
            }
            if (nextframe.inventory && !openedInventory && gui == null) {
                mc.displayGuiScreen(new GuiInventory(mc.player));
            }
            pFrame = nextframe;
        }

    }

    private static void updateKeybinds(KeyFrame frame, KeyFrame framenext) {
    	updateLK(frame.leftClick, mc.gameSettings.keyBindAttack);
        updateRK(frame.rightClick, mc.gameSettings.keyBindUseItem);
        mc.gameSettings.keyBindForward.pressed = frame.forwardKeyDown;
        mc.gameSettings.keyBindBack.pressed = frame.backKeyDown;
        mc.gameSettings.keyBindLeft.pressed = frame.leftKeyDown;
        mc.gameSettings.keyBindRight.pressed = frame.rightKeyDown;
        mc.gameSettings.keyBindJump.pressed = frame.jump;
        mc.gameSettings.keyBindSneak.pressed = frame.sneak;
        mc.gameSettings.keyBindSprint.pressed = frame.sprint;
        mc.gameSettings.keyBindInventory.pressed = frame.inventory; //Read Sprint Key from File
        if (mc.gameSettings.keyBindInventory.pressed) {
            mc.gameSettings.keyBindInventory.pressTime = -1;
        }
        mc.player.inventory.currentItem = frame.slot;                    //Read Inventory Slot from File etc...
        
        
        mc.player.rotationPitch = frame.pitch;
        mc.player.rotationYaw = betweenyaw;
        float framer=uncalc(framenext.yaw, mc.player.rotationYaw);
        betweenyaw=framer;
        /*if(fillerHeadrotations) {
        	setBetweenRotation(frame.pitch, mc.player.rotationYaw, framenext.pitch, framer);
        }*/
    }

    private void moveMouse(int x, int y) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int i1 = scaledResolution.getScaledWidth();
        int j1 = scaledResolution.getScaledHeight();


        int newX = (mc.displayWidth * x) / i1;
        int newY = (mc.displayHeight * (j1 - y - 1)) / j1;
        Mouse.setCursorPosition(newX, newY);
    }
    
    private static void updateLK(String pressed, KeyBinding keybind) {
    	mc.gameSettings.keyBindAttack.pressed = (pressed.equals("pLK")||pressed.equals("hLK"));
    	if(pressed.startsWith("pLK")) {
    		if(pressed.startsWith("pLK[")) {
    			String[] split = pressed.split("(\\[|\\])");
    			setPressed(keybind, Integer.parseInt(split[1]));
    			return;
    		}
    		setPressed(keybind, 1);
    	}
    }
    
    private static void updateRK(String pressed, KeyBinding keybind) {
    	mc.gameSettings.keyBindUseItem.pressed = (pressed.equals("pRK")||pressed.equals("hRK"));
    	if(pressed.startsWith("pRK")) {
    		if(pressed.startsWith("pRK[")) {
    			String[] split = pressed.split("(\\[|\\])");
    			setPressed(keybind, Integer.parseInt(split[1]));
    			return;
    		}
    		setPressed(keybind, 1);
    	}
    }
    
    private static void setPressed(KeyBinding keybind, int presstime) {
    	keybind.pressTime=presstime;
    }
    
    public void unpressAllKeys() {
    	mc.gameSettings.keyBindAttack.pressed = false;
    	 mc.gameSettings.keyBindForward.pressed = false;
         mc.gameSettings.keyBindBack.pressed = false;
         mc.gameSettings.keyBindLeft.pressed = false;
         mc.gameSettings.keyBindRight.pressed = false;
         mc.gameSettings.keyBindJump.pressed = false;
         mc.gameSettings.keyBindSneak.pressed = false;
         mc.gameSettings.keyBindSprint.pressed = false;
         mc.gameSettings.keyBindInventory.pressed = false;
    }
    /*private static void setBetweenRotation(float currentpitch, float currentyaw, float nextpitch, float nextyaw) {
    	//Get the distance between coordinates
    	float pitchtomove=nextpitch-currentpitch;
    	float yawtomove=nextyaw-currentyaw;
    	//Get the increments to show smooth camerapan
    	TASEvents.incrementsPitch=pitchtomove/12;
    	TASEvents.incrementsYaw=yawtomove/12;
    }*/
}
