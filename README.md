## Pocuter App Converter (Java Version)

A converter to convert an ESP32 flash file into a pocuter app.

#### Switches:
-id       [App ID]
-image    [image filename]
-version  [X.Y.Z]
- meta    [metadata filename]
-help

#### Meta file example (ini file):
[APPDATA]
Name=Stopwatch
Author=Pocuter Team

#### Getting the image file in Arduino:
Go to "Sketch" / "Export compiled binary" and after the Sketch is compiled, you will find the bin file in your Sketch directory.

#### App ID
The ID should be an unique ID for your app. Please select a number greater than 100000 to avoid overlapping with official apps. If your app makes it to the app store the number will change.
