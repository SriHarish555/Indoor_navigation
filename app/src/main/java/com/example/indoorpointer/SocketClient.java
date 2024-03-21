package com.example.indoorpointer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String serverAddress;
    private int serverPort;

    public SocketClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
        }
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
    }

    public void send(String message) throws IOException {
        outputStream.write(message.getBytes());
    }

    public String receive() throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            return new String(buffer, 0, bytesRead);
        }catch ( Exception e){
            System.out.println("----------------Processs ABORTED-----------------------");
            return "NO DATA";
        }
    }
}
