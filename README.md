# basic build steps
attach phone with enabled debugging.
install dev-util/android-sdk-update-manager 

  $ /opt/android-sdk-update-manager/platform-tools/adb logcat
run as root if permission fail. resulting adb process can be used as user

* cp <dspace-client/build/*> assets/www/
* ./android.sh <choose version>
* ./build.sh    # ant clean, build debug, install
* ./starts.sh   # send intent
