package com.example.vpn;

import java.io.FileDescriptor;
import java.net.Socket;
import java.io.OutputStream;

public class TunnelThread implements Runnable {

    FileDescriptor tunFd;

    public TunnelThread(FileDescriptor fd) {
        this.tunFd = fd;
    }

    @Override
    public void run() {
        try {
            MainActivity.setStatus("Подключение к серверу...");

            Socket socket = new Socket("192.168.0.150", 9000);
            OutputStream out = socket.getOutputStream();

            out.write("HELLO_FROM_ANDROID\n".getBytes());
            out.flush();

            MainActivity.setStatus("🟢 Соединено с Go сервером");

            // держим соединение
            while (true) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            MainActivity.setStatus("🔴 Нет соединения с сервером");
            e.printStackTrace();
        }
    }
}
