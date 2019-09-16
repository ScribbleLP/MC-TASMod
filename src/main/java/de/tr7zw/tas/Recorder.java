package de.tr7zw.tas;

import java.awt.MouseInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class Recorder {

    /**
     * Indicates on which line the recorder is currently at
     */
    public int recordstep = 0;
    /**
     * Variable to see if a recording is currently running.<br>
     * If true, the recording is stopped
     */
    public boolean donerecording = true;
    /**
     * Shows the status of the leftclick.
     * 0=unpressed, 1=pressed, 2=quickpress <br> Used for destinction if leftclick is held or pressed.
     */
    private int clicklefty = 0;
    /**
     * Shows the status of the rightclick.
     * 0=unpressed, 1=pressed, 2=quickpress <br> Used for destinction if rightclick is held or pressed.
     */
    private int clickrighty = 0;
    /**
     * Used to check if a leftclick was held and needs to print rLK in the next tick <br> Used for destinction if leftclick is held or pressed.
     */
    private boolean needsunpressLK = false;
    /**
     * Used to check if a rightclick was held and needs to print rRK in the next tick <br> Used for destinction if leftclick is held or pressed.
     */
    private boolean needsunpressRK = false;
    /**
     * Used to track how often the leftclick button was pressed. If the clicklefty remains 2, these values will be printed
     */
    private int pressquantityLK=0;
    /**
     * Used to track how often the rightclick button was pressed. If the clickrighty remains 2, these values will be printed
     */
    private int pressquantityRK=0;
    private float tickpitch;
    private float tickyaw;
    private String location;
    private ArrayList<KeyFrame> recording = new ArrayList<>();
    private Minecraft mc = Minecraft.getMinecraft();
    /**
     * Variable to check if leftclick was pressed before.<br>
     * Used for destinction if leftclick is held or pressed. <br>
     * If it's true, leftclick was pressed.
     */
    private boolean lkchecker = false;
    /**
     * Variable to check if rightclick was pressed before.<br>
     * Used for destinction if rightclick is held or pressed.<br>
     * If it's true, rightclick was pressed.
     */
    private boolean rkchecker = false;
    private int gui_mouseX;
    private int gui_mouseY;
    private int gui_mouseButton;
    private int gui_slotUnderMouse;
    private char gui_typedChar;
    private int gui_keyCode;
    private long gui_timeSinceLastClick;
    private int gui_released_state;
    private List<GuiFrame> gui_events = new ArrayList<>();


    public Recorder() {
        location = "#StartLocation: " + mc.player.getPositionVector().toString();
        needsunpressLK = false;
        needsunpressRK = false;
    }

    /**
     * Make it, so the yaw is saved between -180 and +180 so it fits with the debug screen
     *
     * @param Yaw to recalculate (something >180 or <-180?)
     * @return calculated Yaw
     */
    public Float recalcYaw(float Yaw) {
        while (Yaw >= 180) Yaw -= 360;
        while (Yaw < -180) Yaw += 360;
        return Yaw;
    }

    /**
     * Main recording function
     */
    @SubscribeEvent
    public void onClientTickEND(TickEvent.ClientTickEvent ev) {
        if (ev.phase == Phase.END && !donerecording) {

        }
    }

    /**
     * Testmethod to let certain inputs record at the end of the tick
     */
    public void onProcessKeybinds() {
        if (!donerecording) {
            GameSettings gameset = mc.gameSettings;
            String leftclack = " ";
            String rightclack = " ";
            //Printing the correct string for leftclick from onMouseClick
            if (clicklefty == 2) {                        //Scenario for clicking and releasing within a tick
                leftclack = "pLK["+pressquantityLK+"]";
                needsunpressLK = true;
            } else if (clicklefty == 1 && !lkchecker) {        //Scenario for clicking and not releasing within a tick
                leftclack = "pLK";
                needsunpressLK = true;
            } else if (clicklefty == 1) {        //Scenario for holding the button when entering a tick. This would be the case if the above (Scenario for clicking and not releasing within a tick) was the tick beforehand
                leftclack = "hLK";
                needsunpressLK = true;
            } else if (needsunpressLK) {                //Scenario when a button was held or pressed and now it's unpressed.
            	leftclack = "rLK";
                needsunpressLK = false;
            }

            //Same as above, just for rightclick
            if (clickrighty == 2) {
                rightclack = "pRK["+pressquantityRK+"]";
                needsunpressRK = true;
            } else if (clickrighty == 1 && !rkchecker) {
                rightclack = "pRK";
                needsunpressRK = true;
            } else if (clickrighty == 1) {
                rightclack = "hRK";
                needsunpressRK = true;
            } else if (needsunpressRK) {
            	rightclack = "rRK";
                needsunpressRK = false;
            }
            tickpitch = mc.player.rotationPitch;
            tickyaw = recalcYaw(mc.player.rotationYaw);


            //Recording the movement
            recording.add(new KeyFrame(gameset.keyBindForward.isKeyDown(), gameset.keyBindLeft.isKeyDown(), gameset.keyBindBack.isKeyDown(), gameset.keyBindRight.isKeyDown(),	//Movement Keys
            		
            		gameset.keyBindJump.isKeyDown(), gameset.keyBindSneak.isKeyDown()||mc.currentScreen.isShiftKeyDown(), gameset.keyBindSprint.isKeyDown()||mc.currentScreen.isCtrlKeyDown(),	//Modifying Movement Keys
                    
            		leftclack, rightclack,	//Mouse Buttons
                    
            		gameset.keyBindDrop.isKeyDown(), gameset.keyBindInventory.isKeyDown(), mc.player.inventory.currentItem,	//Inventory Related Keys
                    
            		tickpitch, tickyaw,	//Head Rotation
                    
            		MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, gui_events));	//Gui
            
            gui_events.add(new GuiFrame(GuiFrame.FrameType.GUI_DUMMY, gui_mouseX, gui_mouseY, gui_mouseButton, gui_slotUnderMouse, gui_typedChar, gui_keyCode, gui_timeSinceLastClick, gui_released_state));

            gui_events = new ArrayList<>();

            /*Check if leftclick was pressed and not released
             * if it was pressed and immediately released in one tick, clicklefty would equal 2 and thus lkchecker would be false*/
            lkchecker= clicklefty == 1;

            //Same for clickrighty
            rkchecker = clickrighty == 1;

            //resetting values after the recording is done
            clicklefty = 0;
            clickrighty = 0;
            pressquantityLK = 0;
            pressquantityRK = 0;
            //Increment the tickcounter
            if (!donerecording) recordstep++;

        }
    }

    /**
     * Method to check if Mousebuttons are pressed or held.
     */
    @SubscribeEvent
    public void onMouseClick(TickEvent.RenderTickEvent ev) {
        if (!donerecording && ev.phase == Phase.START) {
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)) {
                //set to pressed
                clicklefty = 1;
            } else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack) && clicklefty == 1 && !lkchecker) {
                //set to quick press (e.g. pressing 2 times in 2 ticks)
                clicklefty = 2;
                //increase how many times pressed in a tick
                pressquantityLK++;
            } else if (!(clicklefty == 2)) {
                //set to unpressed
                clicklefty = 0;
            }
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)) {
                //set to pressed
                clickrighty = 1;
            } else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem) && clickrighty == 1 && !rkchecker) {
                //set to quick press (e.g. pressing 2 times in 2 ticks)
                clickrighty = 2;
                //increase how many times pressed in a tick
                pressquantityRK++;
            } else if (clickrighty != 2) {
                //set to unpressed
                clickrighty = 0;
            }
        }
    }

    public void saveData(File file) {
        Movie movie = new Movie();
        movie.location = location;
        movie.frames = recording;

        try {
            movie.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
//        try {
//            mapper.writeValue(file, movie);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Saved to: " + file.getAbsolutePath()));
//        } catch (Exception exX) {
//            exX.printStackTrace();
//        }
    }

    public void guiClicked(int mouseX, int mouseY, int mouseButton, Slot slotUnderMouse) {
        gui_mouseX = mouseX;
        gui_mouseY = mouseY;
        gui_mouseButton = mouseButton;
        gui_slotUnderMouse = -1 ;
        gui_events.add(new GuiFrame(GuiFrame.FrameType.GUI_CLICKED, gui_mouseX, gui_mouseY, gui_mouseButton, gui_slotUnderMouse, gui_typedChar, gui_keyCode, gui_timeSinceLastClick, gui_released_state));
    }

    public void guiTyped(char typedChar, int keyCode, Slot slotUnderMouse) {
        gui_typedChar = typedChar;
        gui_keyCode = keyCode;
        gui_slotUnderMouse = -1;
        gui_events.add(new GuiFrame(GuiFrame.FrameType.GUI_TYPED, gui_mouseX, gui_mouseY, gui_mouseButton, gui_slotUnderMouse, gui_typedChar, gui_keyCode, gui_timeSinceLastClick, gui_released_state));
    }

    public void guiClickMoved(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        gui_mouseX = mouseX;
        gui_mouseY = mouseY;
        gui_mouseButton = clickedMouseButton;
        gui_timeSinceLastClick = timeSinceLastClick;
        gui_events.add(new GuiFrame(GuiFrame.FrameType.GUI_MOUSE_DRAGGED, gui_mouseX, gui_mouseY, gui_mouseButton, gui_slotUnderMouse, gui_typedChar, gui_keyCode, gui_timeSinceLastClick, gui_released_state));
    }

    public void guiReleased(int mouseX, int mouseY, int state) {
        gui_mouseX = mouseX;
        gui_mouseY = mouseY;
        gui_released_state = state;
        gui_events.add(new GuiFrame(GuiFrame.FrameType.GUI_RELEASED, gui_mouseX, gui_mouseY, gui_mouseButton, gui_slotUnderMouse, gui_typedChar, gui_keyCode, gui_timeSinceLastClick, gui_released_state));
    }
}
