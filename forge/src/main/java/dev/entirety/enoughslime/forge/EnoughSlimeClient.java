package dev.entirety.enoughslime.forge;

import dev.entirety.enoughslime.common.network.ClientPacketRouter;
import dev.entirety.enoughslime.forge.events.PermanentEventSubscriptions;
import dev.entirety.enoughslime.forge.network.ConnectionToServer;
import dev.entirety.enoughslime.forge.network.NetworkHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

public class EnoughSlimeClient {

    private final NetworkHandler networkHandler;
    private final PermanentEventSubscriptions subscriptions;

    public EnoughSlimeClient(NetworkHandler networkHandler, PermanentEventSubscriptions subscriptions) {
        this.networkHandler = networkHandler;
        this.subscriptions = subscriptions;
    }

    public void register() {
        subscriptions.register(RegisterClientReloadListenersEvent.class, this::onRegisterReloadListenerEvent);
    }

    private void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        ConnectionToServer serverConnection = new ConnectionToServer(networkHandler);

        ClientPacketRouter packetRouter = new ClientPacketRouter(serverConnection);
        networkHandler.registerClientPacketHandler(packetRouter);
    }

}
