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

    private static final String SERVER_IP = "192.168.0.150";
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

            FileInputStream tunIn = new FileInputStream(tunFd);
            FileOutputStream tunOut = new FileOutputStream(tunFd);

            DatagramSocket udp = new DatagramSocket();
            udp.connect(InetAddress.getByName(SERVER_IP), SERVER_PORT);

            // handshake
            byte[] hello = "HELLO_FROM_ANDROID".getBytes();
            udp.send(new DatagramPacket(hello, hello.length));

            MainActivity.setStatus("🟢 VPN активен (UDP)");

            byte[] buffer = new byte[32767];

            while (running) {
                int len = tunIn.read(buffer);
                if (len > 0) {
                    logPacket(buffer, len);

                    // → Go
                    udp.send(new DatagramPacket(buffer, len));

                    // ← Go
                    DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
                    udp.receive(resp);

                    tunOut.write(resp.getData(), 0, resp.getLength());
                }
            }

            udp.close();

        } catch (Exception e) {
            MainActivity.setStatus("🔴 VPN остановлен");
            Log.e(TAG, "Tunnel error", e);
        }
    }

    private void logPacket(byte[] packet, int len) {
        if (len < 20) return;

        int proto = packet[9] & 0xFF;
        String p = proto == 6 ? "TCP" : proto == 17 ? "UDP" : "OTHER";

        String dst =
                (packet[16] & 0xFF) + "." +
                (packet[17] & 0xFF) + "." +
                (packet[18] & 0xFF) + "." +
                (packet[19] & 0xFF);

        Log.i(TAG, "📦 TUN → " + dst + " proto=" + p + " bytes=" + len);
    }
}
