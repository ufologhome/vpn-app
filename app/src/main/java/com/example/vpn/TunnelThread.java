package com.example.vpn;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TunnelThread extends Thread {

    private final FileDescriptor vpnFd;
    private boolean running = true;

    private final String SERVER_IP = "192.168.0.150"; // твой Go-сервер
    private final int SERVER_PORT = 9000;

    public TunnelThread(FileDescriptor fd) {
        this.vpnFd = fd;
    }

    @Override
    public void run() {
        try (FileInputStream in = new FileInputStream(vpnFd);
             FileOutputStream out = new FileOutputStream(vpnFd);
             DatagramSocket socket = new DatagramSocket()) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            byte[] buffer = new byte[1500];

            while (!Thread.currentThread().isInterrupted() && running) {
                int length = in.read(buffer);
                if (length > 0) {
                    DatagramPacket packet = new DatagramPacket(buffer, length, serverAddress, SERVER_PORT);
                    socket.send(packet);
                }

                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                out.write(response.getData(), 0, response.getLength());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
