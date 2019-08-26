package de.tr7zw.tas;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import de.tr7zw.tas.duck.PlaybackInput;
import de.tr7zw.tas.duck.TASGuiContainer;
import de.tr7zw.tas.networking.TeleportMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TAS {
    public static boolean playing_back = false;
    public static Recorder recorder = null;
    public static TASPlayer tasPlayer = null;
    private static Minecraft mc = Minecraft.getMinecraft();
    private static boolean loaded = false;
    private static double x = 0.0;
    private static double y = 0.0;
    private static double z = 0.0;
    private static float pitch = 0;
    private static float yaw = 0;
    private static String FileName = "null";
    private static boolean genname = true;

    public TAS() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static List<KeyFrame> loadData(File tasData) {
        loaded = true;
        try {
            return Movie.read(tasData).frames;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearData() {
        loaded = false;
        if (tasPlayer != null) {
            tasPlayer.breaking = true;
            tasPlayer = null;
        }
        if (recorder != null) {
            MinecraftForge.EVENT_BUS.unregister(recorder);
        }
        recorder = null;
    }

    public static void sendMessage(String msg) {
        try {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent ev) {
        if (ev.phase == Phase.START && ((PlaybackInput) mc).getPlayback() instanceof TASPlayer) {
            if (donePlaying()) {
                clearData();
            }
        }
    }

    @SubscribeEvent()
    public static void onMenu(GuiOpenEvent ev) {
        System.out.printf("Gui Opened %s%n", ev.getGui());
        if (ev.getGui() instanceof GuiMainMenu) {
            TASModLoader.LOGGER.info("Main menu, clearing data");
            clearData();
        } else if (ev.getGui() instanceof GuiContainer) {
            System.out.printf("Gui Container, giving it recorder %s%n", recorder);
            ((TASGuiContainer) ev.getGui()).setRecorder(recorder);
        }

        if (!donePlaying() && (ev.getGui() == null || ev.getGui() instanceof GuiContainer)) {
            tasPlayer.gui = ev.getGui();
        }
    }

    public static void startRecord() {
        genname = true;
        record();
    }

    private static void record() {
        sendMessage("Starting the tas recording!");
        x = mc.player.posX;                            //Saving the position and headrotation where the command was issued... Is needed for '.f'
        y = mc.player.posY;
        z = mc.player.posZ;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
        pitch = mc.player.rotationPitch;
        yaw = mc.player.rotationYaw;
        recorder = new Recorder();
        recorder.donerecording = false;
        recorder.recordstep = 0;
        ((PlaybackInput) mc).setRecorder(recorder);
        MinecraftForge.EVENT_BUS.register(recorder);
    }

    public static void stopRecording() {
        if (doneRecording()) {
            sendMessage(TextFormatting.RED + "No recording running!");
            return;
        }
        sendMessage("Stopped the tas recording!");
        MinecraftForge.EVENT_BUS.unregister(recorder);
        ((PlaybackInput) mc).setRecorder(null);

        if (genname) {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles" + File.separator + "recording_" + System.currentTimeMillis() + ".tas");
            recorder.saveData(file);
            recorder = null;
        } else {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles" + File.separator + FileName + ".tas");
            recorder.saveData(file);
            recorder = null;
        }
    }

    public static void playTAS(File file) {            //Command to play back the tas recording
        if (recorder != null) {
            recorder.recordstep = 0;
        }
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
        tasPlayer = new TASPlayer(loadData(file));
        ((PlaybackInput) mc).setPlayback(tasPlayer);
        sendMessage("Loaded File");
    }

    public static void teleportToTAS(String[] args) {            //Command to Teleport you to the start of the TASfile
        if (Minecraft.getMinecraft().getIntegratedServer() != null) {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles" + File.separator + args[0] + ".tas");
            if (file.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String[] Location = reader.readLine().split("\\(|(, )|\\)");
                    reader.close();
                    TASModLoader.NETWORK.sendToServer(new TeleportMessage(Double.parseDouble(Location[1]), Double.parseDouble(Location[2]), Double.parseDouble(Location[3])));
                    sendMessage("Teleporting...");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                sendMessage(TextFormatting.RED + "File not found: " + file.getAbsolutePath());
            }
        }
    }

    public static void openWorkFolder() {        //Command for opening the correct directory
        try {
            Desktop.getDesktop().open(new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator +
                    "tasfiles"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean doneRecording() {
        return recorder == null || recorder.donerecording;
    }

    public static boolean donePlaying() {
        return tasPlayer == null || tasPlayer.donePlaying;
    }

    public static void stopPlaying() {
        if (!donePlaying()) {
            tasPlayer.breaking = true;
            tasPlayer = null;
        }
    }

    //Unused legacy method... was used in earlier versions
//    public void showHelp(String[] args) {                //Command for help! Will probably added to real commands
//        if (args.length == 1 || args[1].equals("1")) {            //Output for '.help' /.help 1'
//            sendMessage(TextFormatting.YELLOW + "This is a WIP Tool-Assisted-Speedrun (TAS) Mod. It records your inputs and saves them in a file in your minecraft world, which then can be played back."
//                    + "\n" + TextFormatting.AQUA + "Mod by tr7zw and ScribbleLP");
//            sendMessage(TextFormatting.GOLD + "Enter '.help 2' for commands");
//        } else if (args.length == 2 && args[1].equals("2")) {        //Output for '.help 2'
//            sendMessage(TextFormatting.GOLD + "Commands:\n"
//                    + TextFormatting.YELLOW + ".r" + TextFormatting.AQUA + " (Filename)" + TextFormatting.GREEN + " -Starts a recording (Filename is optional)\n\n"
//                    + TextFormatting.YELLOW + ".s" + TextFormatting.GREEN + " -Stops the recording\n\n"
//                    + TextFormatting.YELLOW + ".f" + TextFormatting.GREEN + " -Aborts the recording and tp's you back where you started\n\n"
//                    + TextFormatting.YELLOW + ".p" + TextFormatting.AQUA + " <filename>" + TextFormatting.GREEN + "  -Plays back the recording, don't add a .tas to the filename\n\n"
//                    + TextFormatting.YELLOW + ".b" + TextFormatting.GREEN + " -Aborts the TAS-playback\n\n"
//                    + TextFormatting.YELLOW + ".tp" + TextFormatting.AQUA + " <filename>" + TextFormatting.GREEN + " -Teleports you to the starting location. Can be found in the first line of the .tas file\n\n"
//                    + TextFormatting.YELLOW + ".fd" + TextFormatting.AQUA + " (info)" + TextFormatting.GREEN + " -Disables FallDamage, since taking damage has a chance of desyncing the TAS\n\n"
//                    + TextFormatting.YELLOW + ".folder" + TextFormatting.GREEN + " -Opens the directory where the .tas files will be saved\n\n"
//                    + TextFormatting.YELLOW + ".help" + TextFormatting.AQUA + " <1,2>" + TextFormatting.GREEN + " -Well guess what this does...");
//        } else sendMessage(TextFormatting.RED + "Too many arguments... Did you mean '.help 2'?");
//    }

    public static void startRecord(String[] args) {
        if (args.length == 1) {                                    //Check for bad characters in filenames
            FileName = args[0];
            if (FileName.contains("/")
                    || FileName.contains(".")
                    || FileName.contains("\r")
                    || FileName.contains("\t")
                    || FileName.contains("\0")
                    || FileName.contains("\f")
                    || FileName.contains("`")
                    || FileName.contains("?")
                    || FileName.contains("*")
                    || FileName.contains("\\")
                    || FileName.contains("<")
                    || FileName.contains(">")
                    || FileName.contains("|")
                    || FileName.contains("\"")
                    || FileName.contains(":")) {
                sendMessage(TextFormatting.RED + "Invalid character(s) for your filename");
                genname = true;
                return;
            } else genname = false;

        } else {
            genname = true;
            sendMessage(TextFormatting.RED + "Too many arguments!");
            return;
        }
        record();
    }

    public static void abortRecording() {
        if (doneRecording()) {
            sendMessage(TextFormatting.RED + "No recording running!");
            return;
        }
        sendMessage("Aborting recording!");
        MinecraftForge.EVENT_BUS.unregister(recorder);
        mc.player.sendChatMessage("/tp " + x + " " + y + " " + z + " " + yaw + " " + pitch);            //Teleports you where the /record command was issued
        recorder = null;
    }
}
