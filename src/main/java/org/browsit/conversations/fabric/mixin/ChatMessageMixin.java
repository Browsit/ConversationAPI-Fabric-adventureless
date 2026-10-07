package org.browsit.conversations.fabric.mixin;

import java.util.Iterator;
import org.browsit.conversations.api.Conversations;
import org.browsit.conversations.api.action.ConversationsForwarder;
import org.browsit.conversations.api.data.ChatVisibility;
import org.browsit.conversations.fabric.FabricConversationsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Illusion
 * created on 2/22/2023
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ChatMessageMixin implements ConversationsForwarder<FabricConversationsMod, ServerPlayer> {

    @Shadow
    public ServerPlayer player;

    @Override
    public void register(FabricConversationsMod fabricConversationsMod) {

    }

    // inject the handleChat method, at the HEAD of the method
    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ServerboundChatPacket packet, CallbackInfo ci) {
        String message = packet.message();
        Component text = Component.literal(message);

        // server is declared in ServerCommonPacketListenerImpl (the target's superclass), where
        // @Shadow cannot reach it, so resolve it through the player's level instead.
        final MinecraftServer server = player.level().getServer();
        Iterator<ServerPlayer> recipients = server.getPlayerList().getPlayers().iterator();

        while (recipients.hasNext()) {
            ServerPlayer recipient = recipients.next();

            Conversations.getConversationOf(recipient.getUUID()).ifPresent(conversation -> {
                if (conversation.getChatVisibility() != ChatVisibility.ALL) {
                    recipients.remove();
                }
            });
        }

        Conversations.getConversationOf(player.getUUID()).ifPresent(conversation -> {
            if (conversation.echoOn()) {
                player.sendSystemMessage(text);
            }

            forwardInput(conversation, message, player, ci::cancel);
        });
    }

}