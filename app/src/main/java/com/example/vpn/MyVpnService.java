package com.example.vpn;

import android.net.VpnService;
import android.content.Intent;
import android.os.ParcelFileDescriptor;

public class MyVpnService extends VpnService {

    private Thread tunnelThread;
    private TunnelThread tunnelRunnable;
    private ParcelFileDescriptor tun;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Builder builder = new Builder();
        builder.setSession("UDP-Tunnel");
        tun = builder.establish();

        tunnelRunnable = new TunnelThread();
        tunnelThread = new Thread(tunnelRunnable);
        tunnelThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tunnelRunnable != null) tunnelRunnable.stop();
        tunnelRunnable = null;
        tunnelThread = null;

        try { if (tun != null) tun.close(); } catch (Exception ignored) {}

        MainActivity.setStatus("VPN остановлен");
        super.onDestroy();
    }
}
