package com.xsz.IoT.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xsz.IoT.R;
import com.xsz.IoT.dialog.GaugeSettingsDialog;
import com.xsz.IoT.model.AnalogInput;
import com.xsz.IoT.view.BarChartView;
import com.xsz.IoT.view.RoundGaugeView;
import com.xsz.IoT.view.TrendChartView;

import java.util.ArrayList;
import java.util.List;

public class GaugeAdapter extends RecyclerView.Adapter<GaugeAdapter.GaugeViewHolder> {
    private List<AnalogInput> analogInputs;
    private OnGaugeClickListener listener;
    private List<GaugeViewHolder> activeViewHolders = new ArrayList<>();
    private int columnCount = 4;
    private int screenHeight = 0;

    public interface OnGaugeClickListener {
        void onGaugeClick(AnalogInput analogInput);
    }

    public GaugeAdapter(List<AnalogInput> analogInputs) {
        this.analogInputs = analogInputs;
    }

    public void setOnGaugeClickListener(OnGaugeClickListener listener) {
        this.listener = listener;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    @NonNull
    @Override
    public GaugeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gauge, parent, false);
        
        // Calculate and set dynamic height for gauge items
        adjustGaugeItemHeight(view, parent);
        
        return new GaugeViewHolder(view);
    }

    private void adjustGaugeItemHeight(View itemView, ViewGroup parent) {
        android.util.DisplayMetrics displayMetrics = parent.getContext().getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        
        // Get status bar height dynamically
        int statusBarHeight = getStatusBarHeight(parent.getContext());
        
        // Calculate available height more accurately
        int toolbarHeight = (int) (56 * displayMetrics.density); // Standard toolbar height
        int headerHeight = (int) (100 * displayMetrics.density); // Header section height (status card)
        int paddingHeight = (int) (32 * displayMetrics.density); // Total padding
        int navigationBarHeight = getNavigationBarHeight(parent.getContext());
        
        int availableHeight = screenHeight - statusBarHeight - toolbarHeight - headerHeight - paddingHeight - navigationBarHeight;
        
        // Fixed 2x4 layout: always 2 rows, 4 columns
        int rowCount = 2;
        
        // Calculate item height to fill available space optimally for 2 rows
        int calculatedItemHeight = (availableHeight / rowCount) - (int) (24 * displayMetrics.density); // Account for margins
        
        // Set reasonable bounds for landscape 2x4 layout
        int minItemHeight = (int) (200 * displayMetrics.density); // Minimum 200dp for landscape
        int maxItemHeight = (int) (400 * displayMetrics.density); // Maximum 400dp for landscape
        int itemHeight = Math.max(minItemHeight, Math.min(maxItemHeight, calculatedItemHeight));
        
        // Find the gauge container and set its height
        View gaugeContainer = itemView.findViewById(R.id.gauge_container);
        if (gaugeContainer != null) {
            ViewGroup.LayoutParams params = gaugeContainer.getLayoutParams();
            params.height = (int) (itemHeight * 0.65f); // Gauge takes 65% of item height for better visibility
            gaugeContainer.setLayoutParams(params);
        }
    }

    private int getStatusBarHeight(android.content.Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getNavigationBarHeight(android.content.Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull GaugeViewHolder holder, int position) {
        AnalogInput input = analogInputs.get(position);
        holder.bind(input);
    }
    
    @Override
    public void onViewRecycled(@NonNull GaugeViewHolder holder) {
        super.onViewRecycled(holder);
        // Remove from tracking to prevent memory leaks
        activeViewHolders.remove(holder);
    }

    @Override
    public int getItemCount() {
        return analogInputs.size();
    }

    public void updateData(List<AnalogInput> newData) {
        this.analogInputs = newData;
        notifyDataSetChanged();
    }
    
    public void refreshGauge(int position) {
        // Refresh a specific gauge to apply settings changes
        notifyItemChanged(position);
    }
    
    public void updateAllGaugesDirectly(List<AnalogInput> newData) {
        // Update all gauges directly without rebinding ViewHolders to prevent flickering
        this.analogInputs = newData;  // Update the data reference
        
        for (GaugeViewHolder holder : activeViewHolders) {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && position < newData.size()) {
                AnalogInput input = newData.get(position);
                holder.updateGaugeDirectly(input);
            }
        }
    }

    class GaugeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInputName;
        private TextView tvInputType;
        private TextView tvMappedValue;
        private TextView tvRawValue;
        private TextView tvDescription;
        private RoundGaugeView roundGauge;
        private BarChartView barChart;
        private TrendChartView trendChart;
        private View btnGaugeSettings;

        public GaugeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInputName = itemView.findViewById(R.id.tv_input_name);
            tvInputType = itemView.findViewById(R.id.tv_input_type);
            tvMappedValue = itemView.findViewById(R.id.tv_mapped_value);
            tvRawValue = itemView.findViewById(R.id.tv_raw_value);
            tvDescription = itemView.findViewById(R.id.tv_description);
            roundGauge = itemView.findViewById(R.id.round_gauge);
            barChart = itemView.findViewById(R.id.bar_chart);
            trendChart = itemView.findViewById(R.id.trend_chart);
            btnGaugeSettings = itemView.findViewById(R.id.btn_gauge_settings);
            
            // Track this ViewHolder
            activeViewHolders.add(this);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onGaugeClick(analogInputs.get(position));
                    }
                }
            });

            btnGaugeSettings.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    AnalogInput input = analogInputs.get(position);
                    GaugeSettingsDialog dialog = new GaugeSettingsDialog(
                            itemView.getContext(), 
                            input, 
                            updatedInput -> {
                                // Update the input in the data manager (this will save to preferences)
                                com.xsz.IoT.model.IoTDataManager.getInstance().updateAnalogInput(updatedInput);
                                // Update the input in the list
                                analogInputs.set(position, updatedInput);
                                // Notify that this specific item changed
                                notifyItemChanged(position);
                                // Notify the main activity
                                if (listener != null) {
                                    listener.onGaugeClick(updatedInput);
                                }
                            }
                    );
                    dialog.show();
                }
            });
        }

        public void bind(AnalogInput input) {
            tvInputName.setText(input.getName());
            String inputTypeText = input.getInputType().getDisplayName();
            if (input.isEnabled()) {
                inputTypeText += itemView.getContext().getString(R.string.source_format, input.getDataSource());
            }
            tvInputType.setText(inputTypeText);
            tvDescription.setText(input.getDescription());

            // Check if input is enabled
            if (!input.isEnabled()) {
                // Show "Not Configured" state
                tvMappedValue.setText(itemView.getContext().getString(R.string.not_configured));
                tvRawValue.setText(itemView.getContext().getString(R.string.click_to_configure));
                
                // Hide all gauges
                roundGauge.setVisibility(View.GONE);
                barChart.setVisibility(View.GONE);
                trendChart.setVisibility(View.GONE);
                
                // Show configure message in gauge container
                showConfigureMessage();
                return;
            }

            // Input is enabled, show normal gauge
            tvMappedValue.setText(input.getFormattedValue());
            tvRawValue.setText(input.getFormattedRawValue());

            // Hide all gauges first
            roundGauge.setVisibility(View.GONE);
            barChart.setVisibility(View.GONE);
            trendChart.setVisibility(View.GONE);

            // Show the selected gauge type
            switch (input.getGaugeType()) {
                case ROUND_GAUGE:
                    roundGauge.setVisibility(View.VISIBLE);
                    roundGauge.setValue((float) input.getMappedValue());
                    roundGauge.setRange((float) input.getMinMappedValue(), (float) input.getMaxMappedValue());
                    roundGauge.setUnit(input.getUnit());
                    // Title removed - already shown in card header
                    break;
                case BAR_CHART:
                    barChart.setVisibility(View.VISIBLE);
                    barChart.setValue((float) input.getMappedValue());
                    barChart.setRange((float) input.getMinMappedValue(), (float) input.getMaxMappedValue());
                    barChart.setUnit(input.getUnit());
                    // Title removed - already shown in card header
                    break;
                case TREND_CHART:
                    trendChart.setVisibility(View.VISIBLE);
                    trendChart.setRange((float) input.getMinMappedValue(), (float) input.getMaxMappedValue());
                    trendChart.setUnit(input.getUnit());
                    // Title removed - already shown in card header
                    
                    // Only initialize with history if chart is empty, otherwise just add new data
                    if (trendChart.getDataPointsCount() == 0) {
                        trendChart.initializeWithHistory((float) input.getMappedValue());
                    } else {
                        // Just add the new data point to continue the trend
                        trendChart.setCurrentValue((float) input.getMappedValue());
                    }
                    break;
            }
        }
        
        private void showConfigureMessage() {
            // This will be handled by the gauge container showing a message
            // For now, we'll just hide the gauges and show the configure text
        }
        
        public void updateGaugeDirectly(AnalogInput input) {
            // Update gauge values and displays directly without full rebinding to prevent flickering
            if (!input.isEnabled()) {
                // For disabled inputs, just update the text values
                tvMappedValue.setText(itemView.getContext().getString(R.string.not_configured));
                tvRawValue.setText(itemView.getContext().getString(R.string.click_to_configure));
                return;
            }
            
            // Update the displayed values for all gauge types
            tvMappedValue.setText(input.getFormattedValue());
            tvRawValue.setText(input.getFormattedRawValue());
            
            // Update the specific gauge type directly
            switch (input.getGaugeType()) {
                case ROUND_GAUGE:
                    if (roundGauge.getVisibility() == View.VISIBLE) {
                        roundGauge.setValue((float) input.getMappedValue());
                    }
                    break;
                case BAR_CHART:
                    if (barChart.getVisibility() == View.VISIBLE) {
                        barChart.setValue((float) input.getMappedValue());
                    }
                    break;
                case TREND_CHART:
                    if (trendChart.getVisibility() == View.VISIBLE) {
                        // Only add new data point, don't rebind
                        trendChart.setCurrentValue((float) input.getMappedValue());
                    }
                    break;
            }
        }
    }
    
    // Method to update all gauges directly without recreating views
    public void updateAllGaugesDirectly() {
        for (int i = 0; i < analogInputs.size(); i++) {
            // Find the ViewHolder for this position if it's currently visible
            RecyclerView.ViewHolder viewHolder = recyclerView != null ? 
                recyclerView.findViewHolderForAdapterPosition(i) : null;
            
            if (viewHolder instanceof GaugeViewHolder) {
                GaugeViewHolder gaugeViewHolder = (GaugeViewHolder) viewHolder;
                gaugeViewHolder.updateGaugeDirectly(analogInputs.get(i));
            }
        }
    }
    
    private RecyclerView recyclerView;
    
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
    
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }
}

