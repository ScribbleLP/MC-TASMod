package de.tr7zw.tas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.tr7zw.tas.PlaybackMethod;
import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.duck.PlaybackInput;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput implements PlaybackInput {
    private PlaybackMethod playback;
    private Recorder recorder;

    @Inject(method="updatePlayerMoveState", at=@At(value="HEAD"))
    private void onUpdatePlayer(CallbackInfo ci) {
    	if (recorder != null) {
            this.recorder.onProcessKeybinds();
        }
        if (playback != null) {
            this.playback.updatePlayerMoveState();
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

	@Override
	public void setRecorder(Recorder recorder) {
		this.recorder=recorder;
	}
}
