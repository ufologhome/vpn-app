package com.example.vpn;

import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TunnelThread implements Runnable {

    private static final String TAG = "VPN";
    private static final String SERVER_IP = "192.168.0.150"; // IP Go сервера
    private static final int SERVER_PORT = 9000;

    private final FileDescriptor tunFd;
    private volatile boolean running = true;

    public TunnelThread(FileDescriptor fd) {
        this.tunFd = fd;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            MainActivity.setStatus("Подключение к Go серверу…");

            // Для теста можно сразу без TUN
            DatagramSocket udp = new DatagramSocket();
            udp.connect(InetAddress.getByName(SERVER_IP), SERVER_PORT);

            // handshake
            byte[] hello = "HELLO_FROM_ANDROID".getBytes();
            udp.send(new DatagramPacket(hello, hello.length));

            // ждём OK
            byte[] buffer = new byte[1024];
            DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
            udp.receive(resp);
            String reply = new String(resp.getData(), 0, resp.getLength());

            if ("OK".equals(reply)) {
                MainActivity.setStatus("🟢 Соединено с Go сервером");
            } else {
                MainActivity.setStatus("🔴 Нет соединения с сервером");
            }

            // keep-alive PING
            while (running) {
                byte[] ping = "PING".getBytes();
                udp.send(new DatagramPacket(ping, ping.length));

                DatagramPacket pong = new DatagramPacket(buffer, buffer.length);
                udp.receive(pong);

                Thread.sleep(3000);
            }

            udp.close();
            MainActivity.setStatus("VPN остановлен");

        } catch (Exception e) {
            MainActivity.setStatus("🔴 VPN остановлен");
            Log.e(TAG, "Tunnel error", e);
        }
    }
}
