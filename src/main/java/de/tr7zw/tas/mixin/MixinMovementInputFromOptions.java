package de.tr7zw.tas.mixin;

import de.tr7zw.tas.PlaybackMethod;
import de.tr7zw.tas.duck.PlaybackInput;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput implements PlaybackInput {
    private PlaybackMethod playback = null;

    @Inject(method = "updatePlayerMoveState", at = @At("HEAD"))
    protected void onUpdatePlayerMoveState(CallbackInfo ci) {
        if (playback != null) {
            playback.updatePlayerMoveState();
        }
    }

    @Override
    public PlaybackMethod getPlayback() {
        return playback;
    }

    @Override
    public void setPlayback(PlaybackMethod newPlayback) {
        playback = newPlayback;
    }
}

