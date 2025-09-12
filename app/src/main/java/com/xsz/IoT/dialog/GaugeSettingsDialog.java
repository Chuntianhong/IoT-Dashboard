package com.xsz.IoT.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.xsz.IoT.R;
import com.xsz.IoT.model.AnalogInput;

public class GaugeSettingsDialog extends Dialog {
    private AnalogInput analogInput;
    private OnSettingsChangedListener listener;
    private Context context;

    public interface OnSettingsChangedListener {
        void onSettingsChanged(AnalogInput input);
    }

    public GaugeSettingsDialog(@NonNull Context context, AnalogInput analogInput, OnSettingsChangedListener listener) {
        super(context);
        this.context = context;
        this.analogInput = analogInput;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_gauge_settings, null);
        setContentView(view);

        setupViews(view);
        populateFields();
    }

    private void setupViews(View view) {
        Switch switchEnabled = view.findViewById(R.id.switch_enabled);
        Spinner spinnerDataSource = view.findViewById(R.id.spinner_data_source);
        Spinner spinnerInputType = view.findViewById(R.id.spinner_input_type);
        Spinner spinnerGaugeType = view.findViewById(R.id.spinner_gauge_type);
        EditText etSensorName = view.findViewById(R.id.et_sensor_name);
        EditText etUnit = view.findViewById(R.id.et_unit);
        EditText etMinMapped = view.findViewById(R.id.et_min_mapped);
        EditText etMaxMapped = view.findViewById(R.id.et_max_mapped);
        EditText etDescription = view.findViewById(R.id.et_description);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        // Setup spinners
        setupSpinners(spinnerDataSource, spinnerInputType, spinnerGaugeType);

        // Setup listeners
        switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            analogInput.setEnabled(isChecked);
        });

        spinnerDataSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSource = position + 1; // 1-8
                analogInput.setDataSource(selectedSource);
                
                // Auto-update input type based on data source
                if (selectedSource <= 4) {
                    analogInput.setInputType(AnalogInput.InputType.VOLTAGE_0_10V);
                    spinnerInputType.setSelection(0);
                } else {
                    analogInput.setInputType(AnalogInput.InputType.CURRENT_4_20MA);
                    spinnerInputType.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerInputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                analogInput.setInputType(AnalogInput.InputType.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerGaugeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                analogInput.setGaugeType(AnalogInput.GaugeType.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            // Save all text field values
            analogInput.setName(etSensorName.getText().toString());
            analogInput.setUnit(etUnit.getText().toString());
            analogInput.setDescription(etDescription.getText().toString());
            
            try {
                analogInput.setMinMappedValue(Double.parseDouble(etMinMapped.getText().toString()));
            } catch (NumberFormatException e) {
                // Keep current value if invalid input
            }
            
            try {
                analogInput.setMaxMappedValue(Double.parseDouble(etMaxMapped.getText().toString()));
            } catch (NumberFormatException e) {
                // Keep current value if invalid input
            }

            if (listener != null) {
                listener.onSettingsChanged(analogInput);
            }
            dismiss();
        });
    }

    private void setupSpinners(Spinner dataSourceSpinner, Spinner inputTypeSpinner, Spinner gaugeTypeSpinner) {
        // Data Source Spinner (1-8)
        String[] dataSources = new String[8];
        for (int i = 0; i < 8; i++) {
            int sourceNum = i + 1;
            String inputType = sourceNum <= 4 ? context.getString(R.string.voltage_0_10v) : context.getString(R.string.current_4_20ma);
            dataSources[i] = context.getString(R.string.input_format, sourceNum, inputType);
        }
        ArrayAdapter<String> dataSourceAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_item_dark, dataSources);
        dataSourceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark);
        dataSourceSpinner.setAdapter(dataSourceAdapter);

        // Input Type Spinner
        ArrayAdapter<AnalogInput.InputType> inputTypeAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_item_dark, AnalogInput.InputType.values());
        inputTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark);
        inputTypeSpinner.setAdapter(inputTypeAdapter);

        // Gauge Type Spinner
        ArrayAdapter<AnalogInput.GaugeType> gaugeTypeAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_item_dark, AnalogInput.GaugeType.values());
        gaugeTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark);
        gaugeTypeSpinner.setAdapter(gaugeTypeAdapter);
    }

    private void populateFields() {
        View view = findViewById(android.R.id.content);
        
        Switch switchEnabled = view.findViewById(R.id.switch_enabled);
        Spinner spinnerDataSource = view.findViewById(R.id.spinner_data_source);
        Spinner spinnerInputType = view.findViewById(R.id.spinner_input_type);
        Spinner spinnerGaugeType = view.findViewById(R.id.spinner_gauge_type);
        EditText etSensorName = view.findViewById(R.id.et_sensor_name);
        EditText etUnit = view.findViewById(R.id.et_unit);
        EditText etMinMapped = view.findViewById(R.id.et_min_mapped);
        EditText etMaxMapped = view.findViewById(R.id.et_max_mapped);
        EditText etDescription = view.findViewById(R.id.et_description);

        // Populate fields with current values
        switchEnabled.setChecked(analogInput.isEnabled());
        spinnerDataSource.setSelection(analogInput.getDataSource() - 1); // Convert 1-8 to 0-7
        spinnerInputType.setSelection(analogInput.getInputType().ordinal());
        spinnerGaugeType.setSelection(analogInput.getGaugeType().ordinal());
        etSensorName.setText(analogInput.getName());
        etUnit.setText(analogInput.getUnit());
        etMinMapped.setText(String.valueOf(analogInput.getMinMappedValue()));
        etMaxMapped.setText(String.valueOf(analogInput.getMaxMappedValue()));
        etDescription.setText(analogInput.getDescription());
    }
}
