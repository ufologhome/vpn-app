package com.example.vpn;

import android.util.Log;

import java.io.FileDescriptor;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TunnelThread implements Runnable {

    private static final String TAG = "VPN";

    private static final String SERVER_IP = "192.168.0.150";
    private static final int SERVER_PORT = 9000;

    private final FileDescriptor tunFd; // пока НЕ используется
    private volatile boolean running = true;

    private DatagramSocket udp;

    public TunnelThread(FileDescriptor fd) {
        this.tunFd = fd;
    }

    public void stop() {
        running = false;
        if (udp != null) {
            udp.close();
        }
    }

    @Override
    public void run() {
        try {
            MainActivity.setStatus("Подключение к серверу…");

            udp = new DatagramSocket();
            udp.connect(
                    InetAddress.getByName(SERVER_IP),
                    SERVER_PORT
            );

            // таймаут, чтобы поток не зависал
            udp.setSoTimeout(3000);

            // === HANDSHAKE ===
            send("HELLO");
            MainActivity.setStatus("🟡 Ожидание ответа сервера…");

            String resp = receive();
            if (!"OK".equals(resp)) {
                throw new RuntimeException("Неверный ответ сервера: " + resp);
            }

            MainActivity.setStatus("🟢 VPN подключён");
            Log.i(TAG, "Handshake OK");

            // === KEEPALIVE ===
            while (running) {
                send("PING");
                Log.d(TAG, "PING → server");
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            Log.e(TAG, "VPN error", e);
            MainActivity.setStatus("🔴 VPN отключён");
        } finally {
            if (udp != null) {
                udp.close();
            }
        }
    }

    private void send(String msg) throws Exception {
        byte[] data = msg.getBytes();
        DatagramPacket p = new DatagramPacket(data, data.length);
        udp.send(p);
    }

    private String receive() throws Exception {
        byte[] buf = new byte[64];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        udp.receive(p);
        return new String(p.getData(), 0, p.getLength());
    }
}
