package de.tr7zw.tas.mixin;

import de.tr7zw.tas.TAS;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "runTickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"),
            cancellable = true)
    private void onRunTickMouse(CallbackInfo ci) {
        if (TAS.playing_back){
            ci.cancel();
            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireMouseInput();
        }
    }
}
