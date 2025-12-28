package com.example.vpn;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;

public class MyVpnService extends VpnService {

    private ParcelFileDescriptor vpnInterface;
    private TunnelThread tunnelThread;
    private static final String TAG = "MyVpnService";

    @Override
    public void onCreate() {
        super.onCreate();
        Builder builder = new Builder();
        builder.addAddress("10.0.0.2", 32);
        builder.addRoute("0.0.0.0", 0);
        builder.setMtu(1400);

        try {
            vpnInterface = builder.establish();
            tunnelThread = new TunnelThread(vpnInterface.getFileDescriptor());
            tunnelThread.start();
        } catch (Exception e) {
            Log.e(TAG, "VPN initialization error", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (tunnelThread != null) tunnelThread.interrupt();
            if (vpnInterface != null) vpnInterface.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing VPN", e);
        }
    }
}
