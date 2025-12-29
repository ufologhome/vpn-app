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
            FileInputStream tunIn = new FileInputStream(tunFd);
            FileOutputStream tunOut = new FileOutputStream(tunFd);

            DatagramSocket udp = new DatagramSocket();
            udp.connect(InetAddress.getByName(SERVER_IP), SERVER_PORT);

            // handshake
            byte[] hello = "HELLO_FROM_ANDROID".getBytes();
            udp.send(new DatagramPacket(hello, hello.length));

            // ждём ответа от сервера
            DatagramPacket resp = new DatagramPacket(new byte[64], 64);
            udp.setSoTimeout(5000); // 5 сек
            try {
                udp.receive(resp);
                String r = new String(resp.getData(), 0, resp.getLength());
                if (!"OK".equals(r)) {
                    MainActivity.setStatus("🔴 Нет подключения к серверу");
                    udp.close();
                    return;
                }
            } catch (Exception e) {
                MainActivity.setStatus("🔴 Нет подключения к серверу");
                udp.close();
                return;
            }

            MainActivity.setStatus("🟢 Соединено с Go сервером");

            byte[] buffer = new byte[32767];

            while (running) {
                // TUN → Go
                if (tunIn.available() > 0) {
                    int len = tunIn.read(buffer);
                    if (len > 0) {
                        udp.send(new DatagramPacket(buffer, len));
                    }
                }

                // PING каждые 2 сек
                udp.send(new DatagramPacket("PING".getBytes(), 4));

                // Go → TUN
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                udp.setSoTimeout(2000);
                try {
                    udp.receive(incoming);
                    tunOut.write(incoming.getData(), 0, incoming.getLength());
                } catch (Exception ignored) {}
                
                Thread.sleep(2000);
            }

            udp.close();

        } catch (Exception e) {
            MainActivity.setStatus("🔴 VPN остановлен");
            Log.e(TAG, "Tunnel error", e);
        }
    }
}
