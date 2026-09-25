# Sign Edit

Sign Edit is a client-side Fabric mod that expands Minecraft's sign editor with rich text formatting, color controls, and additional editing options. It supports Minecraft 26.2 and 26.3.

## Features

### Custom sign edit screen

![](https://i.imgur.com/hPBsoe2.png "Custom Edit Screen")

- Format all or selected text as **bold**, *italic*, underlined, strikethrough, or obfuscated. Formatting is preserved while editing text.
- Choose from Minecraft's sign color palette or pick a custom color. Enter a color as a hex value, apply it to the current selection or typing style, and reset to the sign's default color.
- Edit both the front and back of a sign.
- Switch between regular and hanging sign previews and choose the sign's wood type.

### Additional editing features

- Open the editor without placing a sign: point at an existing sign to edit it, or open a blank sign editor when not targeting one.
- Configure automatic line breaks: turn them off, move to the next line when the current one fills, or wrap the last word onto the next line.
- Optionally loop from the last line back to the first when line wrapping reaches the end.
- Settings are available through Mod Menu and saved in `config/signedit.json`.
- Tested compatibility with [BigSignWriter](https://modrinth.com/mod/big-sign-writer) and [symbolchat](https://modrinth.com/mod/symbol-chat).

## How to use

1. Install the mod and its requirements listed below, then launch Minecraft.
2. Look at a sign and press **I** (the default **Open Sign Editor** key) to edit it. Change the key in **Options → Controls → Key Binds**. If you are not looking at a sign, the key opens a blank editor.
3. Type in the sign fields as usual. Select text and use the formatting buttons or color controls; these also set the style for text typed next. Use the **Front/Back** button to edit either side.
4. Use the wood and sign-type controls to preview a different sign appearance. Finish with Minecraft's usual Done control to save and close.
5. Open Sign Edit's settings from Mod Menu to adjust automatic line breaks and looping to the first line.

## Requirements and compatibility

- Minecraft **26.2+**
- Fabric Loader **0.19.3** or newer
- Fabric API
- Mod Menu is optional and provides an in-game settings entry

Sign Edit runs on the client. The compatibility with BigSignWriter and symbolchat has been tested; install those mods separately if desired.

## Building

Build for either supported Minecraft version by selecting the target:

```powershell
.\gradlew build -PtargetMinecraft=26.2
.\gradlew build -PtargetMinecraft=26.3
```

The resulting jars are named `sign-edit-mc26.2-<mod version>.jar` and `sign-edit-mc26.3-<mod version>.jar`.

## License

This project is available under the CC0-1.0 license.
