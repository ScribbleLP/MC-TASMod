package de.tr7zw.tas.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TeleportMessage implements IMessage{
		private double Xpos;
		private double Ypos;
		private double Zpos;
		
	public TeleportMessage() {
	}
	
	public TeleportMessage(double Xpos, double Ypos, double Zpos) {
		this.Xpos=Xpos;
		this.Ypos=Ypos;
		this.Zpos=Zpos;
	}
		@Override
		public void fromBytes(ByteBuf buf) {
			Xpos=buf.readDouble();
			Ypos=buf.readDouble();
			Zpos=buf.readDouble();
		}
	
		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeDouble(this.Xpos);
			buf.writeDouble(this.Ypos);
			buf.writeDouble(this.Zpos);
		}
	public double getXpos() {
		return Xpos;
	}
	public double getYpos() {
		return Ypos;
	}
	public double getZpos() {
		return Zpos;
	}
}
