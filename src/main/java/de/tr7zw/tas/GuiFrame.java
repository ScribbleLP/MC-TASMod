package de.tr7zw.tas;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiFrame {
    public FrameType type;
    public int gui_mouseX;
    public int gui_mouseY;
    public int gui_mouseButton;
    public int gui_slotUnderMouse;
    public char gui_typedChar;
    public int gui_keyCode;
    public long gui_timeSinceLastClick;
    public int gui_released_state;

    public GuiFrame(FrameType type, int gui_mouseX, int gui_mouseY, int gui_mouseButton, int gui_slotUnderMouse, char gui_typedChar, int gui_keyCode, long gui_timeSinceLastClick, int gui_released_state) {
        this.type = type;
        this.gui_mouseX = gui_mouseX;
        this.gui_mouseY = gui_mouseY;
        this.gui_mouseButton = gui_mouseButton;
        this.gui_slotUnderMouse = gui_slotUnderMouse;
        this.gui_typedChar = gui_typedChar;
        this.gui_keyCode = gui_keyCode;
        this.gui_timeSinceLastClick = gui_timeSinceLastClick;
        this.gui_released_state = gui_released_state;
    }

    public static GuiFrame unpack(String packed) {
        String[] split = packed.split("%%");
        return new GuiFrame(
                FrameType.valueOf(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]),
                Integer.parseInt(split[3]),
                -1,
                split[5].charAt(0),
                Integer.parseInt(split[4]),
                Long.parseLong(split[6]),
                Integer.parseInt(split[7])
        );
    }

    public String pack() {
        return String.join("%%",
                type.name(),
                Integer.toString(gui_mouseX),
                Integer.toString(gui_mouseY),
                Integer.toString(gui_mouseButton),
                Integer.toString(gui_keyCode),
                Character.toString(gui_typedChar),
                Long.toString(gui_timeSinceLastClick),
                Integer.toString(gui_released_state)
        );
    }

    public static String packList(List<GuiFrame> list) {
        StringBuilder builder  = new StringBuilder();
        for (GuiFrame frame: list) {
            builder.append(frame.pack());
            builder.append("&&");
        }
        return builder.toString();
    }

    public static List<GuiFrame> unpackList(String packed){
        return Arrays.stream(packed.split("&&"))
                .map(GuiFrame::unpack)
                .collect(Collectors.toList());
    }


    public enum FrameType {
        GUI_RELEASED,
        GUI_CLICKED,
        GUI_MOUSE_DRAGGED,
        GUI_TYPED,
        GUI_DUMMY
    }
}
