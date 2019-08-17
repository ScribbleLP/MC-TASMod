package de.tr7zw.tas.networking;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TeleportMessageHandler implements IMessageHandler<TeleportMessage, IMessage>{

	@Override
	public IMessage onMessage(TeleportMessage message, MessageContext ctx) {
		if(ctx.side==Side.SERVER) {
			EntityPlayerMP player =ctx.getServerHandler().player;
			if(player.canUseCommand(2, "kick")) {
				player.getServerWorld().addScheduledTask(() ->{
					player.setPositionAndUpdate(message.getXpos(), message.getYpos(), message.getZpos());
				});
			}
		}
		return null;
	}

}