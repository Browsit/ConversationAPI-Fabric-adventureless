package org.browsit.conversations.fabric.mixin;

import net.minecraft.server.MinecraftServer;
import org.browsit.conversations.fabric.FabricConversations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces {@code ServerLifecycleEvents} so that Fabric API is not required at runtime.
 */
@Mixin(MinecraftServer.class)
public abstract class ServerLifecycleMixin {

    @Inject(method = "loadLevel", at = @At("RETURN"))
    private void conversations$serverStarted(CallbackInfo ci) {
        FabricConversations.init((MinecraftServer) (Object) this);
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void conversations$serverStopping(CallbackInfo ci) {
        FabricConversations.cleanUp();
    }

}