package com.xsz.IoT.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IoTDataManager {
    private static IoTDataManager instance;
    private List<AnalogInput> analogInputs;
    private Random random;
    private boolean isSimulationRunning;
    private Context context;
    private static final String PREFS_NAME = "iot_gauge_settings";
    private static final String KEY_ANALOG_INPUTS = "analog_inputs";

    private IoTDataManager() {
        analogInputs = new ArrayList<>();
        random = new Random();
        isSimulationRunning = false;
        initializeAnalogInputs();
    }
    
    public void setContext(Context context) {
        this.context = context;
        loadSettings();
    }

    public static synchronized IoTDataManager getInstance() {
        if (instance == null) {
            instance = new IoTDataManager();
        }
        return instance;
    }

    private void initializeAnalogInputs() {
        for (int i = 1; i <= 8; i++) {
            AnalogInput input = new AnalogInput(i);
            
            // Set minimal default configuration - user will configure each gauge
            input.setName("Analog Input " + i);
            input.setDescription("Not configured");
            input.setEnabled(false); // Start disabled until user configures
            input.setUnit("");
            input.setMinMappedValue(0);
            input.setMaxMappedValue(100);
            
            analogInputs.add(input);
        }
    }

    public List<AnalogInput> getAnalogInputs() {
        return analogInputs;
    }

    public AnalogInput getAnalogInput(int inputNumber) {
        if (inputNumber >= 1 && inputNumber <= analogInputs.size()) {
            return analogInputs.get(inputNumber - 1);
        }
        return null;
    }

    public void updateAnalogInput(int inputNumber, double rawValue) {
        AnalogInput input = getAnalogInput(inputNumber);
        if (input != null) {
            input.setRawValue(rawValue);
        }
    }

    public void startSimulation() {
        isSimulationRunning = true;
        // In a real implementation, this would start a background thread
        // to continuously update values from ESP board
    }

    public void stopSimulation() {
        isSimulationRunning = false;
    }

    private long simulationStartTime = System.currentTimeMillis();
    
    public void resetSimulationTimer() {
        simulationStartTime = System.currentTimeMillis();
    }
    
    public void generateSimulatedData() {
        long currentTime = System.currentTimeMillis();
        double timeInSeconds = (currentTime - simulationStartTime) / 1000.0;
        
        for (int i = 0; i < analogInputs.size(); i++) {
            AnalogInput input = analogInputs.get(i);
            if (input.isEnabled()) {
                double rawValue;
                
                // Generate more realistic trending data with different patterns for each input
                double baseFrequency = 0.1 + (i * 0.05); // Different frequency for each input
                double amplitude, offset;
                
                if (input.getDataSource() <= 4) {
                    // 0-10V inputs
                    amplitude = 4.0; // ±4V variation
                    offset = 5.0;    // Center at 5V
                    rawValue = offset + amplitude * Math.sin(timeInSeconds * baseFrequency);
                    // Add some noise
                    rawValue += (random.nextDouble() - 0.5) * 0.5;
                    rawValue = Math.max(0, Math.min(10, rawValue));
                } else {
                    // 4-20mA inputs  
                    amplitude = 6.0; // ±6mA variation
                    offset = 12.0;   // Center at 12mA
                    rawValue = offset + amplitude * Math.sin(timeInSeconds * baseFrequency);
                    // Add some noise
                    rawValue += (random.nextDouble() - 0.5) * 0.3;
                    rawValue = Math.max(4, Math.min(20, rawValue));
                }
                
                input.setRawValue(rawValue);
            }
        }
    }

    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }
    
    public void saveSettings() {
        if (context == null) return;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        Gson gson = new Gson();
        String json = gson.toJson(analogInputs);
        editor.putString(KEY_ANALOG_INPUTS, json);
        editor.apply();
    }
    
    private void loadSettings() {
        if (context == null) return;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ANALOG_INPUTS, null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<AnalogInput>>(){}.getType();
            List<AnalogInput> savedInputs = gson.fromJson(json, type);
            
            if (savedInputs != null && savedInputs.size() == 8) {
                analogInputs = savedInputs;
            }
        }
    }
    
    public void updateAnalogInput(AnalogInput input) {
        for (int i = 0; i < analogInputs.size(); i++) {
            if (analogInputs.get(i).getInputNumber() == input.getInputNumber()) {
                analogInputs.set(i, input);
                saveSettings();
                break;
            }
        }
    }
}

