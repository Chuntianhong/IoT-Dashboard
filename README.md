# IoT Dashboard Android App

An Android application for monitoring and visualizing analog sensor data from ESP32 microcontrollers. The app provides real-time data visualization through customizable gauges, bar charts, and trend charts in a landscape-oriented interface.

## 📱 Features

### 🎛️ Multiple Visualization Types
- **Round Gauges**: Classic circular gauges with customizable ranges and units
- **Bar Charts**: Vertical bar displays for quick value assessment  
- **Trend Charts**: Real-time oscilloscope-style trending with scrolling behavior

### ⚙️ Flexible Configuration
- Support for 8 analog inputs (configurable)
- Two input types: 0-10V and 4-20mA signals
- Customizable gauge names, units, and value ranges
- Individual enable/disable control for each input
- Data source mapping (multiple gauges can use the same physical input)

### 📊 Real-time Data Simulation
- Built-in data simulation for testing and demonstration
- Realistic trending patterns with configurable frequencies
- Start/stop simulation control via toolbar menu
- Connection status indicator with last update timestamp

### 🎨 Modern UI Design
- Material Design 3 components
- Dark theme support
- Landscape-only orientation for dashboard viewing
- Grid layout (2 rows × 4 columns) optimized for tablets and large screens
- Custom Nunito font family for enhanced readability

## 🏗️ Architecture

### Project Structure
```
app/src/main/java/com/xsz/IoT/
├── MainActivity.java              # Main dashboard activity
├── SettingsActivity.java         # Configuration interface
├── adapter/
│   ├── GaugeAdapter.java         # RecyclerView adapter for gauge grid
│   └── SettingsAdapter.java      # Settings list adapter
├── dialog/
│   └── GaugeSettingsDialog.java  # Individual gauge configuration
├── model/
│   ├── AnalogInput.java          # Data model for analog inputs
│   └── IoTDataManager.java       # Singleton data management
└── view/
    ├── RoundGaugeView.java       # Custom circular gauge widget
    ├── BarChartView.java         # Custom bar chart widget
    ├── TrendChartView.java       # Custom trending chart widget
    └── NunitoTextView.java       # Custom font text views
```

### Key Components

#### IoTDataManager
- Singleton pattern for centralized data management
- Handles 8 analog inputs with configurable properties
- Persistent settings storage using SharedPreferences
- Real-time data simulation with realistic patterns

#### Custom Views
- **RoundGaugeView**: Circular progress indicator with customizable colors and ranges
- **BarChartView**: Vertical bar chart with background and progress visualization
- **TrendChartView**: Advanced oscilloscope-style chart with scrolling behavior

#### Data Model
- **AnalogInput**: Represents individual sensor inputs with mapping capabilities
- Support for voltage (0-10V) and current (4-20mA) input types
- Automatic value mapping from raw sensor data to engineering units

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API level 24+ (Android 7.0)
- Target SDK: API level 35 (Android 15)
- Java 11+ support

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Chuntianhong/IoT-Dashboard.git
   cd IoT-Dashboard
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" or press Shift+F10
   - The app will install and launch automatically

### Configuration

1. **Launch the app** - You'll see the main dashboard with 8 gauge slots
2. **Start simulation** - Tap the play button in the toolbar to begin data simulation
3. **Configure gauges** - Tap the settings icon on any gauge to customize:
   - Gauge name and description
   - Input type (0-10V or 4-20mA)
   - Display type (Round Gauge, Bar Chart, or Trend Chart)
   - Value range and engineering units
   - Data source mapping

## 🔧 Technical Specifications

### Dependencies
- **AndroidX Libraries**: AppCompat, Material Design, ConstraintLayout, Navigation
- **RecyclerView**: For efficient gauge grid display
- **Gson**: JSON serialization for settings persistence
- **Firebase Crashlytics Build Tools**: Error reporting (build-time only)

### Permissions
- `INTERNET`: For future ESP32 connectivity
- `ACCESS_NETWORK_STATE`: Network status monitoring
- `ACCESS_WIFI_STATE`: WiFi connection management

### Performance Features
- Efficient RecyclerView with ViewHolder pattern
- Direct gauge updates without full adapter refresh
- Optimized custom view drawing with proper invalidation
- Memory-efficient data point management in trend charts

## 📡 ESP32 Integration (Future)

The app is designed to connect to ESP32 microcontrollers for real sensor data:

### Expected Data Format
```json
{
  "inputs": [
    {"channel": 1, "value": 7.25, "type": "0-10V"},
    {"channel": 2, "value": 12.4, "type": "4-20mA"},
    // ... up to 8 channels
  ],
  "timestamp": "2025-09-12T10:30:00Z"
}
```

### Connection Methods
- WiFi TCP/IP communication
- HTTP REST API endpoints
- WebSocket for real-time updates
- MQTT protocol support (planned)

## 🎨 Customization

### Themes and Colors
The app uses a custom color scheme defined in `colors.xml`:
- Primary: Blue (#727cf5)
- Success: Green (#0acf97)  
- Background: Light gray (#f8f9fa)
- Text: Dark gray (#495057)

### Custom Fonts
- Nunito Light, Regular, and Bold variants
- Loaded from `assets/fonts/` directory
- Applied through custom TextView classes

### Layout Adaptation
- Fixed 2×4 grid layout optimized for landscape tablets
- Responsive gauge sizing based on available screen space
- Material Design elevation and shadows

## 🧪 Testing

### Simulation Mode
The built-in simulation generates realistic data patterns:
- Sinusoidal waves with different frequencies per input
- Configurable amplitude and offset values
- Random noise injection for realistic behavior
- Proper range limiting (0-10V, 4-20mA)

### Debug Features
- Trend chart oscilloscope behavior testing
- Detailed logging for data flow analysis
- Connection status simulation
- Performance monitoring hooks

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For questions, issues, or feature requests:
- Create an issue on GitHub
- Contact: [chuntianhong@outlook.com]
- Documentation: [Wiki link]

## 🔄 Version History

- **v1.0** - Initial release with simulation and basic gauge support
- **Future**: ESP32 connectivity, data logging, alarm systems

---

**Built with ❤️ for Industrial IoT Applications**
