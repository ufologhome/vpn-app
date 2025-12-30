package com.example.vpn;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

public class TunnelThread implements Runnable {

    private final FileDescriptor tunFd;

    public TunnelThread(FileDescriptor fd) {
        this.tunFd = fd;
    }

    @Override
    public void run() {
        try {
            MainActivity.setStatus("Подключение к Go серверу...");

            FileInputStream tunIn = new FileInputStream(tunFd);
            FileOutputStream tunOut = new FileOutputStream(tunFd);

            Socket sock = new Socket("192.168.0.150", 9000);

            MainActivity.setStatus("🟢 Подключено к Go");

            byte[] buf = new byte[32767];

            while (!Thread.interrupted()) {
                int n = tunIn.read(buf);
                if (n > 0) {
                    sock.getOutputStream().write(buf, 0, n);
                    int r = sock.getInputStream().read(buf);
                    if (r > 0) {
                        tunOut.write(buf, 0, r);
                    }
                }
            }

        } catch (Exception e) {
            MainActivity.setStatus("🔴 VPN остановлен");
            e.printStackTrace();
        }
    }
}
