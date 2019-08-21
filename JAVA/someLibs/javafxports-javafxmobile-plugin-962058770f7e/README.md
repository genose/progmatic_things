# javafxmobile-plugin #

The javafxmobile-plugin is a gradle plugin that unifies the building of JavaFX applications for three different target platforms:

* desktop
* android
* ios

The currently published version is `1.3.17`.

### Getting started ###

If you need information about how to get started with the javafxmobile gradle plugin, we refer you to [Getting Started page](https://docs.gluonhq.com/javafxports/#_getting_started).

### External dependencies ###

The plugin will automatically download several tools to be able to make builds for the different platforms. Below is a listing of all the default dependencies:

#### Android ####

* Android SDK: the android sdk must be downloaded manually because it can not be automated in the plugin. The default android platform that is being built for is `android-27`.
* Dalvik SDK: contains the port of JavaFX 8 to android. The plugin currently depends on `org.javafxports:dalvik-sdk:8.60.12` and `org.javafxports:jfxdvk:8.60.12`.
* Retrolambda: the plugin uses the Retrolambda gradle plugin to transform the code to java 6 compatible bytecode and currently depends on `net.orfjackal.retrolambda:retrolambda:2.5.3`.

#### iOS ####
* iOS SDK: contains the port of JavaFX 8 to iOS. The plugin currently depends on `org.javafxports:ios-sdk:8.60.12`.
* MobiDevelop: the plugin uses MobiDevelop's fork of RoboVM to compile the code to iOS compatible bytecode and currently depends on `com.mobidevelop.robovm:robovm-dist:tar.gz:nocompiler:2.3.1`.

#### Resources ####

* JavaFX ports: https://bitbucket.org/javafxports/8u-dev-rt
* Android SDK: http://developer.android.com/sdk/index.html
* Retrolambda: https://github.com/orfjackal/retrolambda
* MobiDevelop: http://robovm.mobidevelop.com/
