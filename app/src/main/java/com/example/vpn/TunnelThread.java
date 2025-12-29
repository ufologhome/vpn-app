package com.example.vpn;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TunnelThread implements Runnable {

    private static final String TAG = "VPN";
    private volatile boolean running = true;
    private static final String SERVER_IP = "192.168.0.150"; // IP Go сервера
    private static final int SERVER_PORT = 9000;

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            MainActivity.setStatus("Подключение к серверу…");

            DatagramSocket udp = new DatagramSocket();
            udp.connect(InetAddress.getByName(SERVER_IP), SERVER_PORT);

            // handshake
            byte[] hello = "HELLO".getBytes();
            udp.send(new DatagramPacket(hello, hello.length));

            byte[] buf = new byte[1024];
            DatagramPacket resp = new DatagramPacket(buf, buf.length);
            udp.receive(resp);

            String reply = new String(resp.getData(), 0, resp.getLength());
            if ("OK".equals(reply)) {
                MainActivity.setStatus("🟢 Соединено с сервером");
            } else {
                MainActivity.setStatus("🔴 Ошибка соединения");
            }

            // PING loop
            while (running) {
                byte[] ping = "PING".getBytes();
                udp.send(new DatagramPacket(ping, ping.length));
                udp.receive(resp);

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
