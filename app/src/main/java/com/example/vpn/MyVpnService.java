package com.example.vpn;

import android.net.VpnService;
import android.content.Intent;
import android.os.ParcelFileDescriptor;

public class MyVpnService extends VpnService {

    ParcelFileDescriptor tun;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Builder builder = new Builder();
        builder.setSession("WG-Lite")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0);

        tun = builder.establish();

        MainActivity.setStatus("VPN запущен");

        new Thread(new TunnelThread(tun.getFileDescriptor())).start();

        return START_STICKY;
    }
}
