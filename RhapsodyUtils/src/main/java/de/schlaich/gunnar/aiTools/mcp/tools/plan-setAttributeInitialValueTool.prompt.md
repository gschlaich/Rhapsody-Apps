# Plan: SetAttributeInitialValueTool

## Übersicht
MCP-Tool zum Ändern des initialen Werts (Default Value) eines IRPAttribute in IBM Rhapsody.

## Tool-Name
`rhapsody-set-attribute-initial-value`

## Beschreibung
Sets the initial value (default value) of an IRPAttribute identified by the given GUID (UUID). Returns whether the value was set successfully or an error message if it failed.

## Eingangsparameter

| Parameter | Typ    | Erforderlich | Beschreibung                                      |
|-----------|--------|--------------|---------------------------------------------------|
| `id`      | string | Ja           | Die GUID (UUID) des IRPAttribute                  |
| `value`   | string | Ja           | Der neue initiale Wert für das Attribut           |

## Ausgangsparameter

### Erfolgsfall
```json
{
  "content": {
    "status": "success",
    "element": "<vollständiger Pfad zum Element>",
    "attributeName": "<Name des Attributs>",
    "requestedValue": "<angefordeter Wert>",
    "actualValue": "<tatsächlich gesetzter Wert>"
  }
}
```

### Fehlerfälle

#### Element nicht gefunden
```json
{
  "error": "Element not found for GUID: <id>"
}
```

#### Element ist kein Attribut
```json
{
  "error": "Element is not an attribute. Found: <MetaClass>"
}
```

#### Fehler beim Setzen des Werts
```json
{
  "error": "Failed to set initial value: <Fehlermeldung>"
}
```

## Implementierungsdetails

1. **GUID-Auflösung**: Verwendet `RhapsodyClient.byGUID(id)` um das Element zu finden
2. **Typ-Prüfung**: Stellt sicher, dass das Element ein `IRPAttribute` ist
3. **Wert setzen**: Verwendet `IRPAttribute.setDefaultValue(newValue)`
4. **Verifikation**: Liest den Wert mit `getDefaultValue()` zurück und vergleicht

## Abhängigkeiten

- `com.telelogic.rhapsody.core.IRPAttribute`
- `com.telelogic.rhapsody.core.IRPModelElement`
- `de.schlaich.gunnar.aiTools.mcp.RhapsodyClient`

## Registrierung

Das Tool wird in `McpStarter.java` registriert:
```java
registry.register(new SetAttributeInitialValueTool(client, aTraceAction));
```

## Dateien

- **Tool-Implementierung**: `SetAttributeInitialValueTool.java`
- **Registrierung**: `McpStarter.java`

## Beispielaufruf

```json
{
  "method": "tools/call",
  "params": {
    "name": "rhapsody-set-attribute-initial-value",
    "arguments": {
      "id": "GUID_12345678-1234-1234-1234-123456789ABC",
      "value": "42"
    }
  }
}
```
