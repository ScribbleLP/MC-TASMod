package de.tr7zw.tas.mixin;

import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.duck.TASGuiContainer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen implements TASGuiContainer {

    private Recorder recorder;

    @Shadow
    @Nullable
    public abstract Slot getSlotUnderMouse();

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (recorder != null) {
            recorder.guiClicked(mouseX, mouseY, mouseButton, this.getSlotUnderMouse());
        }
    }

    @Inject(method = "mouseClickMove", at = @At("HEAD"))
    private void onMouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick, CallbackInfo ci) {
        if (recorder != null) {
            recorder.guiClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (recorder != null) {
            recorder.guiTyped(typedChar, keyCode, this.getSlotUnderMouse());
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onMouseReleased(int mouseX, int mouseY, int state, CallbackInfo ci) {
        if (recorder != null) {
            recorder.guiReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void setRecorder(Recorder newRecorder) {
        recorder = newRecorder;
        System.out.println("Got new recorder: " + recorder);
    }

    @Override
    public void callMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void callKeyPressed(char typedChar, int keyCode) throws IOException {
        this.keyTyped(typedChar, keyCode);
    }

    @Override
    public void callMouseClickMoved(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void callMouseReleased(int mouseX, int mouseY, int state) {
        this.mouseReleased(mouseX, mouseY, state);
    }
}
