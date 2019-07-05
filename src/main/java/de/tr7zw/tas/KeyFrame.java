package de.tr7zw.tas;

public class KeyFrame {
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
    public boolean gui_clicked;
    public int gui_mouseX;
    public int gui_mouseY;
    public int gui_mouseButton;
    public int gui_slotUnderMouse;
    public boolean gui_typed;
    public char gui_typedChar;
    public int gui_keyCode;
    public boolean gui_clickmoved;
    public long gui_timeSinceLastClick;
    public boolean gui_released;

    @SuppressWarnings("unused")
    public KeyFrame() {
    } //So that jackson can unpack keyframes

    public KeyFrame(boolean forwardKeyDown, boolean backKeyDown, boolean leftKeyDown, boolean rightKeyDown,
                    boolean jump, boolean sneak, boolean sprint, boolean drop, boolean inventory, float pitch, float yaw,
                    boolean leftClick, boolean rightClick, int slot, int mousex, int mousey,
                    int gui_slotUnderMouse, boolean gui_clicked, int gui_mouseX, int gui_mouseY, int gui_mouseButton,
                    boolean gui_typed, char gui_typedChar, int gui_keyCode, boolean gui_clickmoved, long gui_timeSinceLastClick, boolean gui_released) {
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
        this.gui_clicked = gui_clicked;
        this.gui_mouseX = gui_mouseX;
        this.gui_mouseY = gui_mouseY;
        this.gui_mouseButton = gui_mouseButton;
        this.gui_slotUnderMouse = gui_slotUnderMouse;
        this.gui_typed = gui_typed;
        this.gui_typedChar = gui_typedChar;
        this.gui_keyCode = gui_keyCode;
        this.gui_clickmoved = gui_clickmoved;
        this.gui_timeSinceLastClick = gui_timeSinceLastClick;
        this.gui_released = gui_released;
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
                Integer.parseInt(split[19]),
                Integer.parseInt(split[20]),
                0,
                split[14].equals("gC"),
                Integer.parseInt(split[21]),
                Integer.parseInt(split[22]),
                Integer.parseInt(split[23]),
                split[16].equals("gT"),
                split[18].charAt(0),
                Integer.parseInt(split[24]),
                split[15].equals("gCM"),
                Long.parseLong(split[25]),
                split[17].equals("gR")
        );
    }

    public String pack() {
        StringBuilder builder = new StringBuilder();
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
                gui_clicked ? "gC" : " ", //14
                gui_clickmoved ? "gCM" : " ", //15
                gui_typed ? "gT" : " ", //16
                gui_released ? "gR" : " ", //17
                Character.toString(gui_typedChar), //18
                Integer.toString(mouseX), //19
                Integer.toString(mouseY), //20
                Integer.toString(gui_mouseX), //21
                Integer.toString(gui_mouseY), //22
                Integer.toString(gui_mouseButton), //23
                Integer.toString(gui_keyCode), //24
                Long.toString(gui_timeSinceLastClick) //25
        );

        return join;
    }
}
