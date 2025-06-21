# T-Mobile Home Internet Control
A cross-platform app using JetBrains Compose to view and control the following T-Mobile Home Internet gateways:
- Arcadyan KVD21
- Arcadyan TMOG4AR
- Sagemcom Fast 5688W
- Sercomm TMOG4SE
- Nokia 5G21

# Support
Enjoying the app? [Donate here](https://www.paypal.com/donate/?hosted_button_id=EWAPDSENZ7U44).

# Translating
HINT Control uses Crowdin to manage translations. [Help translate to your language here](https://crowdin.com/project/hint-control)!

[![Crowdin](https://badges.crowdin.net/hint-control/localized.svg)](https://crowdin.com/project/hint-control)

# Installing
For Windows, macOS, and Linux, you can download from https://hintcontrol.zwander.dev.
Alternatively, you can use the steps below to manually download a release.

Check the [Releases page](https://github.com/zacharee/HINTControl/releases) for binaries.

## Android
The Android version is available on the [Google Play Store](https://play.google.com/store/apps/details?id=dev.zwander.arcadyankvd21control),

Alternatively, you can download `HINTControl_Android_<VERSION>.apk` and install it.

## iOS
The iOS version is available on the [App Store](https://apps.apple.com/us/app/hint-control/id6449951339).

## Windows
- On Intel or AMD devices, download the .zip ending in `windows-amd64`.
- On ARM64 devices, download the .zip ending in `windows-aarch64`.

## macOS
- On Intel Macs, download the .zip ending in `mac-amd64`.
- On Apple Silicon Macs, download the .zip ending in `mac-aarch64`.

## Linux
- On Debian-based systems, download the `.deb` file.
- On other Linux distros, download the `.tar.gz` file.

For Intel or AMD devices, download the `amd64` variant. For ARM64 devices, choose `aarch64` or `arm64`.

# Usage Notes
## iOS
On iOS, when you enable Snapshots from the Settings page, the stored snapshots will be available in a JSON file from the Files app.

To access the JSON:
1. Open the Files app.
2. Tap the "Browse" tab in the bottom right.
3. Tap the "Browse" tab again to back out of "iCloud Drive".
4. Choose "On My iPhone".
5. Tap "HINT Control".
6. Copy or share `snapshots.json` as needed.

# Support
1. For questions about your T-Mobile Home Internet service, such as outages, speed, or billing, you'll need to contact [T-Mobile support](https://www.t-mobile.com/contact-us).
2. If there's a bug or a crash in HINT Control, [open a new issue](https://github.com/zacharee/ArcadyanKVD21Control/issues) with as much detail as possible.
3. If you'd like to request a new feature, please check the open and closed issues before creating a new one. Be aware that it might not be possible to implement.
4. For anything else, send me an email at <zachary@zwander.dev>.

# Building
## Desktop
HINT Control makes use of [Conveyor](https://www.hydraulic.dev/) to create binaries for different desktop platforms.

Conveyor can build for Windows and Linux from any host OS, but macOS is required to build for macOS.

1. To build, first download and install Conveyor from the link above.
2. Next, open a terminal to the project's root directory.
3. Run `./gradlew :desktop:build` (`.\gradlew.bat :desktop:build` on Windows).
4. Run the following command based on your target system.  
   4.1. Intel/AMD (x86) Windows: `conveyor -Kapp.machines=windows.amd64 make windows-zip`.  
   4.2. ARM64 Windows: `conveyor -Kapp.machines=windows.arm64 make windows-zip`.  
   4.3. x86 Debian: `conveyor -Kapp.machines=linux.amd64 make debian-package`.  
   4.4. ARM64 Debian: `conveyor -Kapp.machines=linux.arm64 make debian-package`.  
   4.5. x86 Linux: `conveyor -Kapp.machines=linux.amd64 make linux-tarball`.  
   4.6. ARM64 Linux: `conveyor -Kapp.machines=linux.arm64 make linux-tarball`.  
   4.7. Intel Macs: `conveyor -Kapp.machines=mac.amd64 make unnotarized-mac-zip`.  
   4.8. Apple Silicon Macs: `conveyor -Kapp.machines=mac.arm64 make unnotarized-mac-zip`.
5. Check the `output` folder in the root of the project for the binary.

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
HINT Control uses Bugsnag for error reporting.

<a href="https://www.bugsnag.com"><img src="https://assets-global.website-files.com/607f4f6df411bd01527dc7d5/63bc40cd9d502eda8ea74ce7_Bugsnag%20Full%20Color.svg" width="200"></a>
