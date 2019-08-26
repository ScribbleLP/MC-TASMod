package de.tr7zw.tas.commands;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.tr7zw.tas.TAS;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Failc extends CommandBase {

    public void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "fail";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/fail,/f (while recording)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("f");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            return;
        }
        if (args.length == 0) {
            if (TAS.doneRecording()) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Wrong usage. This command is used to abort a running recording!"));
            } else {
                TAS.abortRecording();
            }
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Too many arguments!"));
        }
    }
}
