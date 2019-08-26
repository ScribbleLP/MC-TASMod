package de.tr7zw.tas.commands;

import java.util.ArrayList;
import java.util.List;

import de.tr7zw.tas.InfoGui;
import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASEvents;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Tasmodc extends CommandBase {

    public List<String> emptyList(List<String> full) {
        while (full.size() != 0) {
            full.remove(0);
        }
        return full;
    }

    @Override
    public String getName() {
        return "tasmod";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tasmod falldamage [info], folder, help [1,2]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || args.length == 1 && args[0].equals("help")
                || (args.length == 1 && args[0].equals("1"))
                || (args.length == 2 && (args[0].equals("help") && args[1].equals("1")))) {            //Output for '.help' /.help 1'
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in .minecraft/saves/tasfiles, which then can be played back."
                    + "\n" + TextFormatting.AQUA + "Mod by tr7zw and ScribbleLP"));
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Enter '/tasmod help 2' for commands"));
        } else if (args.length == 1 && args[0].equals("help2")
                || (args.length == 1 && args[0].equals("2"))
                || (args.length == 2 && (args[0].equals("help") && args[1].equals("2")))) {        //Output for '.help 2'
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Commands:\n"
                    + TextFormatting.YELLOW + "/record,/rec,/r" + TextFormatting.AQUA + " (Filename)" + TextFormatting.GREEN + " -Starts a recording (Filename is optional)\n\n"
                    + TextFormatting.YELLOW + "/record (again)" + TextFormatting.GREEN + " -Stops the recording\n\n"
                    + TextFormatting.YELLOW + "/fail,/f" + TextFormatting.GREEN + " -Aborts the recording and tp's you back where you started\n\n"
                    + TextFormatting.YELLOW + "/play,/p" + TextFormatting.AQUA + " <filename>" + TextFormatting.GREEN + "  -Plays back the recording, don't add a .tas to the filename\n\n"
                    + TextFormatting.YELLOW + "/play (again)" + TextFormatting.GREEN + " -Aborts the TAS-playback\n\n"
                    + TextFormatting.YELLOW + "/tasmod falldamage" + TextFormatting.AQUA + " (info)" + TextFormatting.GREEN + " -Disables FallDamage, since taking damage has a chance of desyncing the TAS\n\n"
                    + TextFormatting.YELLOW + "/tasmod folder" + TextFormatting.GREEN + " -Opens the directory where the .tas files will be saved\n\n"
                    + TextFormatting.YELLOW + "/tasmod gui" + TextFormatting.GREEN + " Enables disables the gui"));
        } else if (args.length == 1 && args[0].equals("folder")) {
            TAS.openWorkFolder();
        } else if (args.length == 1 && args[0].equals("falldamage")) {
            if (!TASEvents.FallDamage) {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Falldamage enabled"));
                TASEvents.FallDamage = true;
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Falldamage " + TextFormatting.RED + TextFormatting.BOLD + "disabled"));
                TASEvents.FallDamage = false;
            }
        } else if (args.length == 2 && args[0].equals("falldamage") && args[1].equals("info")) {
            if (TASEvents.FallDamage) {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Fall Damage is enabled."));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Fall Damage is currently" + TextFormatting.RED + TextFormatting.BOLD + " disabled." + TextFormatting.RESET + TextFormatting.GREEN + " Taking Fall Damage has a chance to desync the TAS"));
            }
        } else if (args.length == 1) {
            if (args[0].equals("gui")) {
                InfoGui.Infoenabled = !InfoGui.Infoenabled;
            } else if (args[0].equals("strokes")) {
                InfoGui.Strokesenabled = !InfoGui.Strokesenabled;
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          BlockPos targetPos) {
        List<String> tab = new ArrayList<String>();
        if (args.length == 1) {

            return getListOfStringsMatchingLastWord(args, "help", "folder", "falldamage", "gui", "strokes");
        } else if (args.length == 2) {
            if (args[0].equals("help")) {
                return getListOfStringsMatchingLastWord(args, "2");
            } else if (args[0].equals("falldamage")) {
                return getListOfStringsMatchingLastWord(args, "info");
            } else return super.getTabCompletions(server, sender, args, targetPos);

        } else return super.getTabCompletions(server, sender, args, targetPos);

    }
}


