package top.xrondev.lab.nearbychat.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.xrondev.lab.nearbychat.R;
import top.xrondev.lab.nearbychat.adapter.ChannelAdapter;
import top.xrondev.lab.nearbychat.ui.chat.ChatActivity;
import top.xrondev.lab.nearbychat.utils.NearbyConnectionHelper;

public class MainActivity extends AppCompatActivity implements ChannelAdapter.ItemClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private ChannelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permissions in runtime
        boolean permissionGranted = checkPermissions();
        if(!permissionGranted) {
            Log.e("Permission", "Permissions not granted");
        }

        // Nearby Connection helper class
        NearbyConnectionHelper connectionHelper = NearbyConnectionHelper.getInstance(this);

        // TODO: TRY_CATCH the APIException
        connectionHelper.startAdvertising();
        connectionHelper.startDiscovery();


        // channels
        ArrayList<String> channels = new ArrayList<>();
        channels.add("Public Channel");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new ChannelAdapter(this, channels);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adding a divider between items in the RecyclerView
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
    }

    // method called when an item in the RecyclerView is clicked
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("channel_name", adapter.getItem(position));
        startActivity(intent);
    }

    public boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
        }
        return hasPermissions(permissions);
    }

    private boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.w("Permission", permissions[i] + " not granted");
                    }
                }
            }
        }
    }
}