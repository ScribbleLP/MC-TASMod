package me.guichaguri.tastickratechanger;

import me.guichaguri.tastickratechanger.api.TickrateAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TickrateEvents {
private int tickcount=0;


	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent ev) {
		if (ev.phase == Phase.START) {
			if (FMLCommonHandler.instance().getMinecraftServerInstance()!=null&&TickrateAPI.getClientTickrate() <= TickrateChanger.TICKCOUNTERBOARDER) {
				if(!(Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu)) {
					tickcount++;
					TickrateChanger.LOGGER.info("Tickcount: " + tickcount);
				}
			}else if((FMLCommonHandler.instance().getMinecraftServerInstance()==null||TickrateAPI.getClientTickrate()>TickrateChanger.TICKCOUNTERBOARDER)&&tickcount!=0) {
				tickcount=0;
			}
			
			if(TickrateChanger.ADVANCE_TICK) {
				TickrateAPI.handlePausingGame(true);
				TickrateChanger.ADVANCE_TICK=false;
			}
		}
	}
}
