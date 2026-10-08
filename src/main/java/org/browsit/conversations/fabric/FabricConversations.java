package org.browsit.conversations.fabric;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.browsit.conversations.api.Conversations;
import org.browsit.conversations.fabric.util.LegacyTextComponent;
import org.browsit.conversations.impl.audience.ConversationAudienceImpl;
import org.browsit.conversations.impl.provider.ConversationsProviderImpl;

/**
 * @author Illusion
 * created on 2/22/2023
 * <p>
 * Fabric wrapper for {@link Conversations}.
 */
public class FabricConversations {

    private static boolean initialized;

    /**
     * Initalizes the Conversations API.
     */
    public static void init(MinecraftServer server) {
        if (initialized) throw new IllegalStateException("Conversations(Fabric) API already initialized");

        Conversations.init(ConversationsProviderImpl.create(uuid -> new ConversationAudienceImpl(uuid, message -> {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.sendSystemMessage(LegacyTextComponent.from(message));
            }
        })));
        initialized = true;
    }

    /**
     * Cleans up the Conversations API.
     */
    public static void cleanUp() {
        if (!initialized)
            throw new IllegalStateException("Conversations(Fabric) API not initialized");

        Conversations.cleanUp();
        initialized = false;
    }

}