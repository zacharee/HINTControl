# T-Mobile Home Internet Control
A cross-platform app using JetBrains Compose to view and control the Arcadyan KVD21 and Sagemcom Fast 5688W T-Mobile Home Internet gateways.

# Installing
Check the Releases page for binaries.

## Android
Download `android-release.apk` and install it.

## Windows
Download the .zip ending in `windows-amd64`.

Native ARM64 Windows builds aren't currently available.

## macOS
 - On Intel Macs, download the .zip ending in `mac-amd64`.
 - On Apple Silicon Macs, download the .zip ending in `mac-aarch64`.

Extract the downloaded ZIP, then right-click the .app file and choose "Open". The first try will likely result in an error. Close the error dialog and try again and you should see a dialog with an "Open" button show up.

## Linux
 - On Debian-based systems, download the `.deb` file.
 - On other Linux distros, download the `.tar.gz` file.

Native ARM64 binaries for Linux aren't currently available.

## iOS
Download `Hint.Control.ipa`.

Since this .ipa isn't signed, you'll need to use something like [Sideloadly](https://sideloadly.io/) or [AltStore](https://altstore.io/) to install it.

# Building
## Desktop
HINT Control makes use of [Conveyor](https://www.hydraulic.dev/) to create binaries for different desktop platforms.

Conveyor can build for Windows and Linux from any host OS, but macOS is required to build for macOS.

1. To build, first download and install Conveyor from the link above.
2. Next, open a terminal to the project's root directory.
3. Run the following command based on your target system.
  3.1. Windows: `conveyor make windows-zip`.
  3.2. Debian: `conveyor make debian-package`.
  3.3. Linux: `conveyor make linux-tarball`.
  3.4. Intel Macs: `conveyor -Kapp.machines=mac.amd64 make unnotarized-mac-zip`.
  3.5. Apple Silicon Macs: `conveyor -Kapp.machines=mac.arm64 make unnotarized-mac-zip`.
4. Check the `output` folder in the root of the project for the binary.

## Android
1. Open the project in Android Studio.
2. Build the APK or run the Android configuration with your device plugged in.

## iOS
### Creating an IPA
1. Open `iosApp/iosApp.xcworkspace` in Xcode.
2. Open the "Product" menu, choose "Destination", and select "Any iOS Device (arm64)".
3. Open the "Product" menu again and select "Archive".
4. Once the build completes, a new window will open listing the newly-created archive.
5. Right click the archive and select "Show in Finder".
6. Right click the highlighted file in Finder and select "Show Package Contents".
7. Go to `Products/Applications` and create a new folder called `Payload`.
8. Drag `HINT Control.app` into the `Payload` folder.
9. Right click `Payload` and choose "Compress".
10. Change the resulting .zip file extension to .ipa.

### Running on a Simulator or Device
1. Open `iosApp/iosApp.xcworkspace` in Xcode.
2. Choose your target device in the top of the window.
3. Press the "Play" button to the left.

# Error Reporting
Bifrost uses Bugsnag for error reporting.

<a href="https://www.bugsnag.com"><img src="https://assets-global.website-files.com/607f4f6df411bd01527dc7d5/63bc40cd9d502eda8ea74ce7_Bugsnag%20Full%20Color.svg" width="200"></a>
