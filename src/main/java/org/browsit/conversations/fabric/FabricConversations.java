package org.browsit.conversations.fabric;

import org.browsit.conversations.api.Conversations;
import org.browsit.conversations.impl.provider.AdventureConversationsProvider;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;

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

        Conversations.init(AdventureConversationsProvider.create(MinecraftServerAudiences.of(server)));
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
