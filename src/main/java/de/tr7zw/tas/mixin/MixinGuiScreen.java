package de.tr7zw.tas.mixin;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

@Mixin(GuiScreen.class)
public class MixinGuiScreen extends Gui{
	
	@Inject(method="isShiftKeyDown", at = @At("HEAD"))
	private static boolean onisShiftclickdown(CallbackInfoReturnable<Boolean> ci) {
		if(Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
			return true;
		}
		return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
	}
}
