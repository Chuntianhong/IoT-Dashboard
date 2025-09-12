package com.xsz.IoT;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.xsz.IoT.adapter.SettingsAdapter;
import com.xsz.IoT.model.AnalogInput;
import com.xsz.IoT.model.IoTDataManager;

import java.util.List;

public class SettingsActivity extends AppCompatActivity implements SettingsAdapter.OnInputConfigListener {

    private IoTDataManager dataManager;
    private SettingsAdapter settingsAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.analog_input_settings));
        }

        // Initialize data manager
        dataManager = IoTDataManager.getInstance();

        // Setup RecyclerView
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_settings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        settingsAdapter = new SettingsAdapter(dataManager.getAnalogInputs(), this);
        recyclerView.setAdapter(settingsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInputConfigChanged(AnalogInput input) {
        // Configuration is automatically saved in the data manager
        // Show feedback to user
        Snackbar.make(recyclerView, getString(R.string.configuration_updated_format, input.getName()), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Return to main activity with updated data
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
