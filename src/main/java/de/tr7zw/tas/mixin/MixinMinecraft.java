package de.tr7zw.tas.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.tr7zw.tas.PlaybackMethod;
import de.tr7zw.tas.Recorder;
import de.tr7zw.tas.TAS;
import de.tr7zw.tas.TASModLoader;
import de.tr7zw.tas.duck.PlaybackInput;
import me.guichaguri.tastickratechanger.TASTickEvent;
import me.guichaguri.tastickratechanger.TickrateChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements PlaybackInput {
    private PlaybackMethod playback;
    private Recorder recorder;
    
	@Shadow 
	public abstract void runTickKeyboard();
	
    @Inject(method = "runTickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"),
            cancellable = true)
    private void onRunTickMouse(CallbackInfo ci) {
        if (TAS.playing_back){
            ci.cancel();
            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireMouseInput();
        }
    }
    //TASTickratechanger
	@Inject(method = "runGameLoop", at = @At(value="HEAD"))
	private void processKeybind(CallbackInfo ci) {
		TickrateChanger.TASTIMER.updateTimer();
		for (int j = 0; j < Math.min(10, TickrateChanger.TASTIMER.elapsedTicks); ++j)
        {
            MinecraftForge.EVENT_BUS.post(new TASTickEvent());
        }
		if(TickrateChanger.TICKS_PER_SECOND==0) {
			this.runTickKeyboard();
		}
	}
	@ModifyConstant(method = "runTickMouse", constant= {@Constant(longValue=200L)})
	/**
	 * Used to fix The Mousewheel in lower Tickrates
	 * @param ignored
	 * @return
	 */
	private long fixMouseWheel(long ignored) {
		if(TickrateChanger.TICKS_PER_SECOND!=0) {
			return (long)Math.max(4000F/TickrateChanger.TICKS_PER_SECOND, 200L);
		}else {
			return 200L;
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
    
    
    @Shadow
	GuiScreen currentScreen;
    @Shadow
    RenderGlobal renderGlobal;
    @Shadow
    GameSettings gameSettings;
    @Shadow
    EntityRenderer entityRenderer;
    @Shadow
    public abstract Entity getRenderViewEntity();
    @Shadow
    EntityPlayerSP player;
    @Shadow
    GuiIngame ingameGUI;
    @Shadow
    PlayerControllerMP playerController;
    @Shadow
    Tutorial tutorial;
    @Shadow
    public abstract void displayGuiScreen(@Nullable GuiScreen guiScreenIn);
    @Shadow
    public abstract NetHandlerPlayClient getConnection();
    @Shadow
    public abstract void clickMouse();
    @Shadow
    public abstract void rightClickMouse();
    @Shadow
    public abstract void middleClickMouse();
    @Shadow
    int rightClickDelayTimer;
    @Shadow
    boolean inGameHasFocus;
    @Shadow
    public abstract void sendClickBlockToController(boolean leftClick);
    
    private void processKeyBinds()
    {
    	if(!(TickrateChanger.TICKS_PER_SECOND==0&&currentScreen instanceof GuiChat)) {
	        for (; this.gameSettings.keyBindTogglePerspective.isPressed(); this.renderGlobal.setDisplayListEntitiesDirty())
	        {
	            ++this.gameSettings.thirdPersonView;
	
	            if (this.gameSettings.thirdPersonView > 2)
	            {
	                this.gameSettings.thirdPersonView = 0;
	            }
	
	            if (this.gameSettings.thirdPersonView == 0)
	            {
	                this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
	            }
	            else if (this.gameSettings.thirdPersonView == 1)
	            {
	                this.entityRenderer.loadEntityShader((Entity)null);
	            }
	        }
	
	        while (this.gameSettings.keyBindSmoothCamera.isPressed())
	        {
	            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
	        }
	
	        for (int i = 0; i < 9; ++i)
	        {
	            boolean flag = this.gameSettings.keyBindSaveToolbar.isKeyDown();
	            boolean flag1 = this.gameSettings.keyBindLoadToolbar.isKeyDown();
	        
	            if (this.gameSettings.keyBindsHotbar[i].isPressed())
	            {
	                if (this.player.isSpectator())
	                {
	                    this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
	                }
	                else if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag)
	                {
	                    this.player.inventory.currentItem = i;
	                }
	                else
	                {
	                    GuiContainerCreative.handleHotbarSnapshots(Minecraft.getMinecraft(), i, flag1, flag);
	                }
	            }
	        }
	        while (this.gameSettings.keyBindInventory.isPressed())
	        {
	            if (this.playerController.isRidingHorse())
	            {
	                this.player.sendHorseInventory();
	            }
	            else
	            {
	                this.tutorial.openInventory();
	                this.displayGuiScreen(new GuiInventory(this.player));
	            }
	        }
	        while (this.gameSettings.keyBindAdvancements.isPressed())
	        {
	            this.displayGuiScreen(new GuiScreenAdvancements(this.player.connection.getAdvancementManager()));
	        }
	
	        while (this.gameSettings.keyBindSwapHands.isPressed())
	        {
	            if (!this.player.isSpectator())
	            {
	                this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
	            }
	        }
	
	        while (this.gameSettings.keyBindDrop.isPressed())
	        {
	            if (!this.player.isSpectator())
	            {
	                this.player.dropItem(GuiScreen.isCtrlKeyDown());
	            }
	        }
	        boolean flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
	        if (flag2)
	        {
	            while (this.gameSettings.keyBindChat.isPressed())
	            {
	                this.displayGuiScreen(new GuiChat());
	            }
	
	            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed())
	            {
	                this.displayGuiScreen(new GuiChat("/"));
	            }
	        }
    	}else {
    		KeyBinding.unPressAllKeys();
    	}
        if (this.player.isHandActive())
        {
            if (!this.gameSettings.keyBindUseItem.isKeyDown())
            {
                this.playerController.onStoppedUsingItem(this.player);
            }

            label109:

            while (true)
            {
                if (!this.gameSettings.keyBindAttack.isPressed())
                {
                    while (this.gameSettings.keyBindUseItem.isPressed())
                    {
                        ;
                    }

                    while (true)
                    {
                        if (this.gameSettings.keyBindPickBlock.isPressed())
                        {
                            continue;
                        }

                        break label109;
                    }
                }
            }
        }
        else
        {
            while (this.gameSettings.keyBindAttack.isPressed())
            {
                this.clickMouse();
            }

            while (this.gameSettings.keyBindUseItem.isPressed())
            {
                this.rightClickMouse();
            }

            while (this.gameSettings.keyBindPickBlock.isPressed())
            {
                this.middleClickMouse();
            }
        }

        if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive())
        {
            this.rightClickMouse();
        }

        this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
    }
}
