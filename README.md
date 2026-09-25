# Sign Edit

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## Minecraft versions

Build against either supported version by selecting the Minecraft target:

```powershell
.\gradlew build -PtargetMinecraft=26.2
.\gradlew build -PtargetMinecraft=26.3
```

The resulting jars are named `sign-edit-mc26.2-<mod version>.jar` and `sign-edit-mc26.3-<mod version>.jar`. Version-specific implementations for APIs that changed between releases live under `src/versions/26.2/java` and `src/versions/26.3/java`; shared code remains under `src/client/java`.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
