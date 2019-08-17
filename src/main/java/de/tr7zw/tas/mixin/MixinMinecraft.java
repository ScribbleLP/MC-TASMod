package de.tr7zw.tas.mixin;

import de.tr7zw.tas.PlaybackMethod;
import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASPlayer;
import de.tr7zw.tas.duck.PlaybackInput;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft implements PlaybackInput {
    private PlaybackMethod playback;
    private Recorder recorder;

    @Inject(method = "runTickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"),
            cancellable = true)

    private void onRunTickMouse(CallbackInfo ci) {
        if (TAS.playing_back){
            ci.cancel();
            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireMouseInput();
        }
    }

    @Inject(method = "processKeyBinds", at = @At("HEAD"))
    private void onProcessKeyBinds(CallbackInfo ci) {
//        if (TAS.playing_back) {
//            TASPlayer.keyBindsHook();
//        }
        if (recorder != null) {
            this.recorder.onProcessKeybinds();
        }
        if (playback != null) {
            this.playback.updatePlayerMoveState();
        }
    }

    @Override
    public PlaybackMethod getPlayback() {
        return this.playback;
    }

    @Override
    public void setPlayback(PlaybackMethod newPlayback) {
        this.playback = newPlayback;
    }

    @Override
    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }
}
