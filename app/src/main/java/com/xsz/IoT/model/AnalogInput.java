package com.xsz.IoT.model;

public class AnalogInput {
    private int inputNumber;
    private String name;
    private double rawValue;
    private double mappedValue;
    private String unit;
    private InputType inputType;
    private GaugeType gaugeType;
    private double minRange;
    private double maxRange;
    private double minMappedValue;
    private double maxMappedValue;
    private String description;
    private boolean isEnabled;
    private int dataSource; // 1-8, indicates which physical input this gauge uses

    public enum InputType {
        VOLTAGE_0_10V("0-10V"),
        CURRENT_4_20MA("4-20mA");

        private final String displayName;

        InputType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum GaugeType {
        ROUND_GAUGE("Round Gauge"),
        BAR_CHART("Bar Chart"),
        TREND_CHART("Trend Chart");

        private final String displayName;

        GaugeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public AnalogInput(int inputNumber) {
        this.inputNumber = inputNumber;
        this.name = "Analog Input " + inputNumber;
        this.rawValue = 0.0;
        this.mappedValue = 0.0;
        this.unit = "";
        this.inputType = inputNumber <= 4 ? InputType.VOLTAGE_0_10V : InputType.CURRENT_4_20MA;
        this.gaugeType = GaugeType.ROUND_GAUGE;
        this.minRange = inputNumber <= 4 ? 0.0 : 4.0;
        this.maxRange = inputNumber <= 4 ? 10.0 : 20.0;
        this.minMappedValue = 0.0;
        this.maxMappedValue = 100.0;
        this.description = "";
        this.isEnabled = true;
        this.dataSource = inputNumber; // Default to same as input number
    }

    // Getters and Setters
    public int getInputNumber() { return inputNumber; }
    public void setInputNumber(int inputNumber) { this.inputNumber = inputNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRawValue() { return rawValue; }
    public void setRawValue(double rawValue) { 
        this.rawValue = rawValue;
        this.mappedValue = mapValue(rawValue);
    }

    public double getMappedValue() { return mappedValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public InputType getInputType() { return inputType; }
    public void setInputType(InputType inputType) { this.inputType = inputType; }

    public GaugeType getGaugeType() { return gaugeType; }
    public void setGaugeType(GaugeType gaugeType) { this.gaugeType = gaugeType; }

    public double getMinRange() { return minRange; }
    public void setMinRange(double minRange) { this.minRange = minRange; }

    public double getMaxRange() { return maxRange; }
    public void setMaxRange(double maxRange) { this.maxRange = maxRange; }

    public double getMinMappedValue() { return minMappedValue; }
    public void setMinMappedValue(double minMappedValue) { this.minMappedValue = minMappedValue; }

    public double getMaxMappedValue() { return maxMappedValue; }
    public void setMaxMappedValue(double maxMappedValue) { this.maxMappedValue = maxMappedValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public int getDataSource() { return dataSource; }
    public void setDataSource(int dataSource) { this.dataSource = dataSource; }

    private double mapValue(double rawValue) {
        if (maxRange == minRange) return minMappedValue;
        
        double normalizedValue = (rawValue - minRange) / (maxRange - minRange);
        return minMappedValue + normalizedValue * (maxMappedValue - minMappedValue);
    }

    public String getFormattedValue() {
        return String.format("%.2f %s", mappedValue, unit);
    }

    public String getFormattedRawValue() {
        return String.format("%.2f %s", rawValue, inputType.getDisplayName());
    }
}

