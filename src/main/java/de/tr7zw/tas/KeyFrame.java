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


    public KeyFrame(boolean forwardKeyDown, boolean leftKeyDown, boolean backKeyDown, boolean rightKeyDown,
                    boolean jump, boolean sneak, boolean sprint, boolean leftClick, boolean rightClick, boolean drop, boolean inventory,int slot, float pitch, float yaw,
                    int mousex, int mousey, List<GuiFrame> gui_states) {
        super();
        this.forwardKeyDown = forwardKeyDown;
        this.leftKeyDown = leftKeyDown;
        this.backKeyDown = backKeyDown;
        this.rightKeyDown = rightKeyDown;
        this.jump = jump;
        this.sneak = sneak;
        this.sprint = sprint;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.drop = drop;
        this.inventory = inventory;
        this.slot = slot;
        this.pitch = pitch;
        this.yaw = yaw;
        this.mouseX = mousex;
        this.mouseY = mousey;
        this.gui_states = gui_states;
    }

    public static KeyFrame unpack(String packed) {
        String[] split = packed.split(",");

        return new KeyFrame(
                split[0].equals("W"),
                split[1].equals("A"),
                split[2].equals("S"),
                split[3].equals("D"),
                split[4].equals("Space"),
                split[5].equals("Shift"),
                split[6].equals("Ctrl"),
                split[7].equals("LK"),
                split[8].equals("RK"),
                split[6].equals("Q"),
                split[10].equals("E"),
                Integer.parseInt(split[11]),
                Float.parseFloat(split[12]),
                Float.parseFloat(split[13]),
                Integer.parseInt(split[14]),
                Integer.parseInt(split[15]),
                GuiFrame.unpackList(split[16])
        );
    }

    public String pack() {
        // W,A,S,D,Space,Shift,Ctrl,LK,RK,Q,E,Slot,Pitch,Yaw,guiEvents

        String join = String.join(",",
                forwardKeyDown ? "W" : " ", //0
                leftKeyDown ? "A" : " ", //1
                backKeyDown ? "S" : " ", //2
                rightKeyDown ? "D" : " ", //3
                jump ? "Space" : " ", //4
                sneak ? "Shift" : " ", //5
                sprint ? "Ctrl" : " ", //6
                leftClick ? "LK" : " ", //7
                rightClick ? "RK" : " ", //8
                drop ? "Q" : " ", //9
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
