package de.tr7zw.tas.commands;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

import de.tr7zw.tas.TAS;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TasTpc extends CommandBase {

    public List<String> getFilenames() {
        List<String> tab = new ArrayList<String>();
        File folder = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + "tasfiles");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            tab.add(listOfFiles[i].getName().replaceAll("\\.tas", ""));
        }
        return tab;
    }

    public void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "tastp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tastp or /tptas <Filename (without .tas. If not set, it generates a filename)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("tptas");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (TAS.doneRecording() && TAS.donePlaying()) {
            if (args.length == 0) {
                sendMessage(TextFormatting.RED + "Wrong usage! Use this command to tp to the start of a TASfile. Add a tasfilename behind this");
                return;
            }
            if (args.length == 1) {
                TAS.teleportToTAS(args);
                return;
            }
            if (args.length > 1) sendMessage(ChatFormatting.RED + "Too many arguments");
        } else if (!TAS.doneRecording()) {
            sendMessage(TextFormatting.RED + "A recording is running. /record or /fail to abort recording");
        } else {
            sendMessage(TextFormatting.RED + "A playback is running. /play to abort");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          BlockPos targetPos) {
        List<String> tab;
        if (args.length == 1) {
            tab = getFilenames();
            if (tab.isEmpty()) {
                sendMessage(TextFormatting.RED + "No files in directory");
                return null;
            }
            return getListOfStringsMatchingLastWord(args, tab);
        } else {
            return super.getTabCompletions(server, sender, args, targetPos);
        }
    }
}
