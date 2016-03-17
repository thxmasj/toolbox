package it.thomasjohansen.toolbox.socket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author thomas@thomasjohansen.it
 */
public class AvailablePort {

    public static int find() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            serverSocket.setReuseAddress(true);
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new FindAvailablePortFailed(e);
        }
    }

    public static class FindAvailablePortFailed extends RuntimeException {

        FindAvailablePortFailed(IOException cause) {
            super("Failed to find available port", cause);
        }

    }

}
