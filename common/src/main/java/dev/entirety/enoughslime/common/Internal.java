package dev.entirety.enoughslime.common;

import com.google.common.base.Preconditions;
import dev.entirety.enoughslime.common.network.IConnectionToClient;
import dev.entirety.enoughslime.common.network.IConnectionToServer;

public final class Internal {

    private static IConnectionToClient clientConnection;
    private static IConnectionToServer serverConnection;

    private Internal() {}

    public static void setClientConnection(IConnectionToClient clientConnection) {
        Internal.clientConnection = clientConnection;
    }

    public static IConnectionToClient getClientConnection() {
        Preconditions.checkState(clientConnection != null, "Client connection has not been created yet.");
        return clientConnection;
    }

    public static void setServerConnection(IConnectionToServer serverConnection) {
        Internal.serverConnection = serverConnection;
    }

    public static IConnectionToServer getServerConnection() {
        Preconditions.checkState(serverConnection != null, "Server connection has not been created yet.");
        return serverConnection;
    }

}
