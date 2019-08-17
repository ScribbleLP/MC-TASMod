package de.tr7zw.tas;

import java.util.List;

public class KeyFrame {
    public List<GuiFrame> gui_states;
    public boolean inventory;
    public boolean forwardKeyDown;        //Where all the Keys are saved!
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneak;
    public float pitch;
    public float yaw;
    public boolean leftClick;
    public boolean rightClick;
    public boolean sprint;
    public boolean drop;
    public int mouseX;
    public int mouseY;
    public int slot;


    public KeyFrame(boolean forwardKeyDown, boolean backKeyDown, boolean leftKeyDown, boolean rightKeyDown,
                    boolean jump, boolean sneak, boolean sprint, boolean drop, boolean inventory, float pitch, float yaw,
                    boolean leftClick, boolean rightClick, int slot, int mousex, int mousey, List<GuiFrame> gui_states) {
        super();
        this.forwardKeyDown = forwardKeyDown;
        this.backKeyDown = backKeyDown;
        this.leftKeyDown = leftKeyDown;
        this.rightKeyDown = rightKeyDown;
        this.jump = jump;
        this.sneak = sneak;
        this.pitch = pitch;
        this.yaw = yaw;
        this.drop = drop;
        this.mouseX = mousex;
        this.mouseY = mousey;
        //if(this.pitch > 90)this.pitch = 90;
        //if(this.pitch < -90)this.pitch = -90;
        //if(this.yaw > 180)this.yaw = 180;
        //if(this.yaw < -180)this.yaw = -180;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.sprint = sprint;
        this.slot = slot;
        this.gui_states = gui_states;
        this.inventory = inventory;

    }

    public static KeyFrame unpack(String packed) {
        String[] split = packed.split(",");

        return new KeyFrame(
                split[0].equals("W"),
                split[1].equals("S"),
                split[2].equals("A"),
                split[3].equals("D"),
                split[4].equals("Space"),
                split[5].equals("Shift"),
                split[9].equals("Ctrl"),
                split[6].equals("Q"),
                split[10].equals("E"),
                Float.parseFloat(split[12]),
                Float.parseFloat(split[13]),
                split[7].equals("LK"),
                split[8].equals("RK"),
                Integer.parseInt(split[11]),
                Integer.parseInt(split[14]),
                Integer.parseInt(split[15]),
                GuiFrame.unpackList(split[16])
        );
    }

    public String pack() {
        // W,S,A,D,Space,Shift,Q,LK,RK,Ctrl,E,Pitch,Yaw,Slot,guiEvents

        String join = String.join(",",
                forwardKeyDown ? "W" : " ", //0
                backKeyDown ? "S" : " ", //1
                leftKeyDown ? "A" : " ", //2
                rightKeyDown ? "D" : " ", //3
                jump ? "Space" : " ", //4
                sneak ? "Shift" : " ", //5
                drop ? "Q" : " ", //6
                leftClick ? "LK" : " ", //7
                rightClick ? "RK" : " ", //8
                sprint ? "Ctrl" : " ", //9
                inventory ? "E" : " ", //10
                Integer.toString(slot), //11
                Float.toString(pitch), //12
                Float.toString(yaw), //13
                Integer.toString(mouseX), //14
                Integer.toString(mouseY), //15
                GuiFrame.packList(this.gui_states)//16
        );

        return join;
    }
}
