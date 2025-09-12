package com.xsz.IoT.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xsz.IoT.R;
import com.xsz.IoT.model.AnalogInput;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {
    private List<AnalogInput> analogInputs;
    private OnInputConfigListener listener;

    public interface OnInputConfigListener {
        void onInputConfigChanged(AnalogInput input);
    }

    private interface TextChangeCallback {
        void onTextChanged(AnalogInput input);
    }

    public SettingsAdapter(List<AnalogInput> analogInputs, OnInputConfigListener listener) {
        this.analogInputs = analogInputs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settings, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        AnalogInput input = analogInputs.get(position);
        holder.bind(input);
    }

    @Override
    public int getItemCount() {
        return analogInputs.size();
    }

    class SettingsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInputName;
        private Switch switchEnabled;
        private Spinner spinnerInputType;
        private Spinner spinnerGaugeType;
        private EditText etSensorName;
        private EditText etUnit;
        private EditText etMinMapped;
        private EditText etMaxMapped;
        private EditText etDescription;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInputName = itemView.findViewById(R.id.tv_input_name);
            switchEnabled = itemView.findViewById(R.id.switch_enabled);
            spinnerInputType = itemView.findViewById(R.id.spinner_input_type);
            spinnerGaugeType = itemView.findViewById(R.id.spinner_gauge_type);
            etSensorName = itemView.findViewById(R.id.et_sensor_name);
            etUnit = itemView.findViewById(R.id.et_unit);
            etMinMapped = itemView.findViewById(R.id.et_min_mapped);
            etMaxMapped = itemView.findViewById(R.id.et_max_mapped);
            etDescription = itemView.findViewById(R.id.et_description);

            setupSpinners();
            setupListeners();
        }

        private void setupSpinners() {
            // Input Type Spinner
            ArrayAdapter<AnalogInput.InputType> inputTypeAdapter = new ArrayAdapter<>(
                    itemView.getContext(), R.layout.spinner_item_dark, AnalogInput.InputType.values());
            inputTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark);
            spinnerInputType.setAdapter(inputTypeAdapter);

            // Gauge Type Spinner
            ArrayAdapter<AnalogInput.GaugeType> gaugeTypeAdapter = new ArrayAdapter<>(
                    itemView.getContext(), R.layout.spinner_item_dark, AnalogInput.GaugeType.values());
            gaugeTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark);
            spinnerGaugeType.setAdapter(gaugeTypeAdapter);
        }

        private void setupListeners() {
            switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                AnalogInput input = analogInputs.get(getAdapterPosition());
                input.setEnabled(isChecked);
                if (listener != null) {
                    listener.onInputConfigChanged(input);
                }
            });

            spinnerInputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AnalogInput input = analogInputs.get(getAdapterPosition());
                    input.setInputType(AnalogInput.InputType.values()[position]);
                    if (listener != null) {
                        listener.onInputConfigChanged(input);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            spinnerGaugeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AnalogInput input = analogInputs.get(getAdapterPosition());
                    input.setGaugeType(AnalogInput.GaugeType.values()[position]);
                    if (listener != null) {
                        listener.onInputConfigChanged(input);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Text change listeners
            etSensorName.addTextChangedListener(createTextWatcher(input -> input.setName(etSensorName.getText().toString())));
            etUnit.addTextChangedListener(createTextWatcher(input -> input.setUnit(etUnit.getText().toString())));
            etDescription.addTextChangedListener(createTextWatcher(input -> input.setDescription(etDescription.getText().toString())));

            etMinMapped.addTextChangedListener(createTextWatcher(input -> {
                try {
                    input.setMinMappedValue(Double.parseDouble(etMinMapped.getText().toString()));
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }));

            etMaxMapped.addTextChangedListener(createTextWatcher(input -> {
                try {
                    input.setMaxMappedValue(Double.parseDouble(etMaxMapped.getText().toString()));
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }));
        }

        private TextWatcher createTextWatcher(TextChangeCallback callback) {
            return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    AnalogInput input = analogInputs.get(getAdapterPosition());
                    callback.onTextChanged(input);
                    if (listener != null) {
                        listener.onInputConfigChanged(input);
                    }
                }
            };
        }

        public void bind(AnalogInput input) {
            tvInputName.setText(itemView.getContext().getString(R.string.analog_input_format, input.getInputNumber()));
            switchEnabled.setChecked(input.isEnabled());
            
            // Set spinner selections
            spinnerInputType.setSelection(input.getInputType().ordinal());
            spinnerGaugeType.setSelection(input.getGaugeType().ordinal());
            
            // Set text fields
            etSensorName.setText(input.getName());
            etUnit.setText(input.getUnit());
            etMinMapped.setText(String.valueOf(input.getMinMappedValue()));
            etMaxMapped.setText(String.valueOf(input.getMaxMappedValue()));
            etDescription.setText(input.getDescription());
        }
    }
}
