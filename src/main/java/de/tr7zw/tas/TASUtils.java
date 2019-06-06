package de.tr7zw.tas;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class TASUtils {
    public static void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
