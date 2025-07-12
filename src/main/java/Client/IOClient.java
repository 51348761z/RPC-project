package Client;

import common.message.RpcRequest;
import common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles client-side I/O for RPC communication.
 * Provides a static method to send an RpcRequest and receive an RpcResponse.
 */
public class IOClient {
    /**
     * Sends an RPC request to the specified host and port.
     * Establishes a new socket connection, writes the request, reads the response,
     * and handles potential I/O or class-not-found exceptions.
     *
     * @param host The RPC server's hostname or IP address.
     * @param port The RPC server's listening port.
     * @param request The RpcRequest object to send.
     * @return The RpcResponse received from the server, or {@code null} on failure.
     */
    public static RpcResponse sendRequest(String host, int port, RpcRequest request) {
        try {
            Socket socket= new Socket(host, port);
            // Set up object streams for sending/receiving
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Write request, flush, and read response
            out.writeObject(request);
            out.flush();
            RpcResponse response = (RpcResponse) in.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
