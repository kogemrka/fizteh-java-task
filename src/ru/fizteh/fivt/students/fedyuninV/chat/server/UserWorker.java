package ru.fizteh.fivt.students.fedyuninV.chat.server;

import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class UserWorker implements Runnable{
    private final Socket socket;
    private final Server server;
    private final ByteBuffer buffer;
    private final Thread userThread;
    private String name;

    public UserWorker(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
        buffer = ByteBuffer.wrap(null);
        userThread = new Thread(this);
        name = null;
    }

    public void start() {
        userThread.start();
    }

    public void kill() {
        try {
            socket.close();
        } catch (Exception ignored) {

        }
        userThread.interrupt();
    }

    public void join() {
        try {
            userThread.join();
        } catch (Exception ignored) {

        }
    }

    public void sendMessage(byte[] bytes) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        } catch (Exception ex) {
        }
    }

    public void run() {
        try {
            InputStream iStream = socket.getInputStream();
            while (!userThread.isInterrupted()) {
                try {
                    Message message = MessageUtils.getMessage(iStream);
                    server.processMessage(message, this);
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
