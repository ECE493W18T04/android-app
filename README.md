# WNHHUD

Repo: https://github.com/ECE493W18T04/android-app

## Usage

### Android app
Open HUDApp folder with Android Studio and build/install the project to the target demo phone. If the project fails to build because of a missing google maven dependency then simply click the link to have android studio automatically correct the dependency. Once the app in installed, accept all permissions on first run (espescially the notification listener.) 

### Hardware
The hardware is already loaded with the latest version of the WNH firmware. The software is currently loaded with the non-flipped version of the software (i.e. the version that does not require a mirror to read.) If you wish to load the flipped version so to use with a piece of glass or a mirror change the following parameter.

In [embedded-firmware/src/GraphicsManager.cpp](https://github.com/ECE493W18T04/android-app/blob/master/embedded-firmware/src/GraphicsManager.cpp#L273) line 273
change `flipped = 0;` to `flipped = 1;`

and to build and load the firmware onto the device, plug in a USB cable and install [platformio](https://platformio.org/) then in the embedded-firmware folder, run the following command `pio run -t upload`

## Demoing

### Initial Setup
* Plug in the DC adapter to power. If power is properly connected then LED 1 on the hardwre should be blinking, if it stops at any time then you need to hit reset on the hardware and reconnect.
* Make sure Bluetooth is on. On the app hit the active mode switch, accept any messages about permissions. At the same time hit Button 1 on the hardware, the display will show "Pairing."
* Once the devices find each other the hardware will display a number that the app will request. On correct entry the app will connect and the hardware will display a clock.

### Demoing
* There are two ways to start the system after the initial pair. Either will start the BLE connection to the hardware.
  * Hit the active switch in the WNH app
  * Start navigation mode in Google maps
* Once connected, several events can trigger the app to display information to the display
  * Music change in Spotify
  * Phone call (some manufacturer phone apps may not work)
  * Google maps directions
  * Time
* Press button 2 to trigger the voice control from the app. Valid commands are
  * `"Set colour to <color name>"` for example "Set colour to red"
  * `"Set brightness to <Brightness>"` where brightness is a number between 1 and 100
  * `"Set auto brightness <on/off>"` to disable or enable autobrightness
  * `"Force <Notification>"` this will override the priority queue (see in app for the current order of the queue.) **this command may not behave as desired as the behavior is not well handled when notifications have not been initially posted**
* Shine a flashlight over the main hardware unit (phone light works fine) when in autobrightness mode to demo how the unit handles ambient brightness changes.
* Other notifications that are supported (but the hardware/software is more complex to setup, due to OpenXC requirement for interfacing with the vehicle)
  * Vehicle Speed
  * Vehicle Fuel level
