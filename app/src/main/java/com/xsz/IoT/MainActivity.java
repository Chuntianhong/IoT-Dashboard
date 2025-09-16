package com.xsz.IoT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.xsz.IoT.adapter.GaugeAdapter;
import com.xsz.IoT.databinding.ActivityMainBinding;
import com.xsz.IoT.model.AnalogInput;
import com.xsz.IoT.model.IoTDataManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GaugeAdapter.OnGaugeClickListener {

    private ActivityMainBinding binding;
    private IoTDataManager dataManager;
    private GaugeAdapter gaugeAdapter;
    private Handler updateHandler;
    private Runnable updateRunnable;
    private boolean isUpdating = false;
    private MenuItem simulationMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Initialize data manager
        dataManager = IoTDataManager.getInstance();
        dataManager.setContext(this);

        // Setup RecyclerView
        setupRecyclerView();

        // Menu will handle simulation control

        // Individual gauge settings are now handled by each gauge card

        // Initialize status
        updateConnectionStatus(true);
        
        // Generate initial data to show values immediately
        //dataManager.generateSimulatedData();
        //gaugeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_iot, menu);
        simulationMenuItem = menu.findItem(R.id.action_simulation);
        updateSimulationMenuItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_simulation) {
            toggleSimulation();
            return true;
        } else if (id == R.id.action_settings) {
            // Handle settings action if needed
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleSimulation() {
        if (!isUpdating) {
            startDataSimulation();
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.simulation_started_msg), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.stop), v -> stopDataSimulation()).show();
        } else {
            stopDataSimulation();
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.simulation_stopped_msg), Snackbar.LENGTH_SHORT).show();
        }
        updateSimulationMenuItem();
    }

    private void updateSimulationMenuItem() {
        if (simulationMenuItem != null) {
            if (isUpdating) {
                simulationMenuItem.setIcon(android.R.drawable.ic_media_pause);
                simulationMenuItem.setTitle(getString(R.string.data_simulation_stopped));
            } else {
                simulationMenuItem.setIcon(android.R.drawable.ic_media_play);
                simulationMenuItem.setTitle(getString(R.string.data_simulation_started));
            }
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_gauges);
        
        // Calculate optimal column count based on screen size
        int columnCount = calculateOptimalColumnCount();
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        recyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        gaugeAdapter = new GaugeAdapter(dataManager.getAnalogInputs());
        gaugeAdapter.setOnGaugeClickListener(this);
        gaugeAdapter.setColumnCount(columnCount);
        recyclerView.setAdapter(gaugeAdapter);
    }

    private int calculateOptimalColumnCount() {
        // Always use 4 columns for 2x4 grid layout (2 rows, 4 columns)
        return 4;
    }

    private void startDataSimulation() {
        if (isUpdating) return;
        
        isUpdating = true;
        // Reset simulation timer for consistent trending data
        dataManager.resetSimulationTimer();
        updateHandler = new Handler(Looper.getMainLooper());
        
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isUpdating) {
                    // Generate simulated data
                    dataManager.generateSimulatedData();
                    
                    // Update UI directly without recreating views
                    gaugeAdapter.updateAllGaugesDirectly();
                    updateLastUpdateTime();
                    
                    // Schedule next update
                    updateHandler.postDelayed(this, 50); // Update every 500ms
                }
            }
        };
        
        updateHandler.post(updateRunnable);
    }

    private void stopDataSimulation() {
        isUpdating = false;
        if (updateHandler != null && updateRunnable != null) {
            updateHandler.removeCallbacks(updateRunnable);
        }
        updateSimulationMenuItem();
    }

    private void updateConnectionStatus(boolean connected) {
        View statusIndicator = findViewById(R.id.status_indicator);
        TextView statusText = findViewById(R.id.tv_connection_status);
        
        if (connected) {
            statusIndicator.setBackgroundResource(R.drawable.circle_green);
            statusText.setText(getString(R.string.connected_to_esp32));
        } else {
            statusIndicator.setBackgroundResource(R.drawable.circle_red);
            statusText.setText(getString(R.string.disconnected_from_esp32));
        }
    }

    private void updateLastUpdateTime() {
        TextView lastUpdateText = findViewById(R.id.tv_last_update);
        SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.time_format), Locale.getDefault());
        lastUpdateText.setText(getString(R.string.last_update_format, timeFormat.format(new Date())));
    }

    @Override
    public void onGaugeClick(AnalogInput analogInput) {
        Snackbar.make(findViewById(android.R.id.content),
                String.format("Clicked on %s", analogInput.getName()),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Settings were accessed, refresh the gauges to show any changes
            refreshGauges();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh gauges when returning to the activity
        refreshGauges();
    }
    
    private void refreshGauges() {
        if (gaugeAdapter != null) {
            // Update the adapter with fresh data from the data manager
            gaugeAdapter.updateData(dataManager.getAnalogInputs());
            updateLastUpdateTime();
        }
    }

    // Orientation change handling removed since app is landscape-only

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDataSimulation();
    }
}