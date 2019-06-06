package de.tr7zw.tas.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recordc extends CommandBase {

    private Minecraft mc = Minecraft.getMinecraft();
    private boolean check = false;

    public List<String> getFilenames() {
        List<String> tab = new ArrayList<String>();
        File folder = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + "tasfiles");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            tab.add(listOfFiles[i].getName().replaceAll("\\.tas", ""));
        }
        return tab;
    }

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/record or /r or /rec [filename] (without .tas. If not set, it generates a filename)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("r", "rec");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            return;
        }
        if (TAS.doneRecording() && TAS.donePlaying()) {
            if (args.length == 0) {
                sender.sendMessage(new TextComponentString("No filename set! Generating one..."));
                TAS.startRecord();
            }
            if (args.length == 1) {
                TAS.startRecord(args);
            }
            if (args.length > 1) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Too many arguments"));
            }
        } else if (!TAS.doneRecording()) {
            TAS.stopRecording();
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "A playback is running. /play to abort"));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          BlockPos targetPos) {
        List<String> tab;
        if (args.length == 1) {
            if (!check) {
                sender.sendMessage(new TextComponentString(TextFormatting.BOLD + "" + TextFormatting.RED + "WARNING!" + TextFormatting.RESET + TextFormatting.RED +
                        " Existing filenames will be overwritten! /fail to abort the recording if you accidentally started one"));
                check = true;
            }
            tab = getFilenames();
            if (tab.isEmpty()) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "No files in directory"));
                return super.getTabCompletions(server, sender, args, targetPos);
            }
            return getListOfStringsMatchingLastWord(args, tab);
        } else return super.getTabCompletions(server, sender, args, targetPos);


    }
}

