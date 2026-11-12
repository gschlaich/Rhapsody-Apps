# Mermaid Viewer Implementation

## Overview
A Mermaid diagram viewer has been successfully added to the RhapsodyUtils project. This allows users to view Mermaid diagrams generated from Rhapsody models in a dedicated viewer window.

## Files Added

### 1. MermaidViewer.java
**Location:** `src/main/java/de/schlaich/gunnar/rhapsody/plantUMLView/MermaidViewer.java`

**Purpose:** Handles the conversion of Mermaid diagram source code to PNG images and displays them in a window.

**Key Features:**
- Converts Mermaid diagrams to PNG format using the Kroki API
- Supports both GET and POST request methods for diagram conversion
- Provides static methods for easy integration with existing code
- Integrates with the existing `showPNG` viewer for displaying rendered diagrams

**Main Methods:**
- `convertMermaidToPNG(String mermaidSource)` - Converts Mermaid source to PNG bytes
- `displayMermaidDiagram(String mermaidSource, String title, boolean aExitOnClose)` - Displays the diagram in a window

**API Used:**
- **Kroki** (https://kroki.io/) - A free, public API for converting diagrams to images
- Supports Mermaid diagram types: flowcharts, sequence diagrams, class diagrams, state diagrams, etc.

## Files Modified

### 1. PlantUMLStarter.java
**Location:** `src/main/java/de/schlaich/gunnar/rhapsody/plantUMLView/PlantUMLStarter.java`

**Changes:**
- Added logic to check if the `aShowMermaid` flag is set
- When Mermaid display is requested, uses `MermaidViewer.displayMermaidDiagram()` to render the diagram
- Returns early to avoid trying to render Mermaid diagrams with PlantUML renderer
- Maintains backward compatibility with existing PlantUML functionality

**Updated Method:**
- `startPlantUML()` - Now supports both PlantUML and Mermaid diagram display

### 2. pom.xml
**Location:** `pom.xml`

**Changes:**
- Added comment section for Mermaid diagram rendering
- Clarified existing GSON dependency which is used for Mermaid rendering

**Dependencies:**
- `com.google.code.gson:gson:2.13.1` - Used for JSON operations if needed for extended functionality

## How It Works

### Diagram Rendering Flow

1. **Diagram Generation:**
   - `PlantUMLStarter.startPlantUML()` is called with a Rhapsody model element and `aShowMermaid=true`
   - `MermaidGenerator` creates the Mermaid diagram source code from the model

2. **Clipboard Copy:**
   - The Mermaid diagram source is copied to the system clipboard for easy pasting elsewhere

3. **Rendering Decision:**
   - If `aShowMermaid=true`, control is passed to `MermaidViewer`
   - Otherwise, the existing PlantUML rendering pipeline is used

4. **API Conversion:**
   - `MermaidViewer.convertMermaidToPNG()` encodes the Mermaid source in Base64
   - Sends request to Kroki API: `https://kroki.io/mermaid/png/{base64-encoded-diagram}`
   - Receives PNG image bytes in response

5. **Display:**
   - PNG image is displayed in a `showPNG` window
   - Window title shows the model element name
   - Inherits all functionality from the existing PlantUML viewer

## Supported Mermaid Diagram Types

The Mermaid viewer supports all diagram types supported by Mermaid.js:

- **Flowcharts** - Sequential flowcharts and decision trees
- **Sequence Diagrams** - Interaction diagrams showing message sequences
- **Class Diagrams** - Object-oriented class hierarchies
- **State Diagrams** - State machines and statecharts
- **Entity Relationship Diagrams** - Database schema visualization
- **Gantt Charts** - Project timeline visualization
- **Pie Charts** - Statistical distribution charts
- **Git Graphs** - Git workflow visualization
- **Requirements Diagrams** - System requirements modeling

## Network Requirements

The MermaidViewer requires internet connectivity to use the Kroki API service. The implementation includes:

- **Timeout handling** - 10-second timeout for connections
- **Fallback mechanism** - If GET fails, automatically tries POST method
- **Error handling** - Graceful error messages if conversion fails
- **User-Agent header** - Identifies requests as browser-compatible

## Integration Example

```java
// Display a Mermaid diagram from a Rhapsody model element
PlantUMLStarter.startPlantUML(rhapsodyApp, selectedElement, false, true);
```

Where:
- `rhapsodyApp` - The Rhapsody application instance
- `selectedElement` - The selected model element to diagram
- `false` - Do not exit on close
- `true` - Show Mermaid diagram instead of PlantUML

## Advantages Over PlantUML

1. **Simpler Syntax** - Mermaid uses simpler, more intuitive diagram syntax
2. **Modern Rendering** - Better visual appearance for modern users
3. **Better Text Rendering** - Clearer text rendering in diagrams
4. **Web-Native** - Easy to share diagrams as web-based content
5. **Extended Features** - Some diagram types not available in PlantUML (Git Graphs, Requirements)

## Fallback and Robustness

- If the Kroki API is unavailable, the viewer will return null
- The application can detect this and take appropriate action
- Error messages are logged to System.err for debugging
- The PlantUML rendering path remains available as alternative

## Future Enhancements

Possible improvements for future versions:

1. **Local Rendering** - Use a local Mermaid library to eliminate internet dependency
2. **Caching** - Cache rendered diagrams to improve performance
3. **Export Options** - Add SVG/PDF export capabilities
4. **Mermaid Customization** - Allow theming and configuration options
5. **Diagram Editing** - Add inline editing capabilities for diagrams
6. **Alternative APIs** - Support other diagram rendering services as fallback

## Troubleshooting

### Diagram Not Displaying
- Check internet connectivity
- Verify Mermaid syntax is valid
- Check System.err for error messages

### Slow Rendering
- Network latency may affect rendering speed
- First render may be slower due to API initialization

### API Unavailability
- Kroki service is down - try again later
- Consider implementing local rendering as alternative

## References

- **Mermaid Documentation:** https://mermaid.js.org/
- **Kroki API:** https://kroki.io/
- **PlantUML Reference:** https://plantuml.com/
- **Rhapsody API Documentation:** Refer to OMROOT documentation

## Support

For issues or questions about the Mermaid viewer:
1. Check the error messages in System.err
2. Verify Mermaid diagram syntax at https://mermaid.live/
3. Test diagram conversion at https://kroki.io/
4. Review this documentation and code comments
