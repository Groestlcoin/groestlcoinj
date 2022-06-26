[![Github Build Status](https://github.com/Groestlcoin/groestlcoinj/workflows/Java%20CI/badge.svg)](https://github.com/Groestlcoin/groestlcoinj/actions)

[![Visit our IRC channel](https://kiwiirc.com/nextclient/irc.libera.chat/groestlcoin.png)](https://kiwiirc.com/nextclient/irc.libera.chat/groestlcoin)

### Welcome to groestlcoinj

The groestlcoinj library is a Java implementation of the Groestlcoin protocol, which allows it to maintain a wallet and send/receive transactions without needing a local copy of Groestlcoin Core. It comes with full documentation and some example apps showing how to use it.

### Technologies

* Java 8+ (needs Java 8 API or Android 6.0 API, compiles to Java 8 bytecode) and Gradle 4.4+ for the `core` module
* Java 8+ and Gradle 4.4+ for `tools` and `examples`
* Java 11+ and Gradle 4.10+ for the JavaFX-based `wallettemplate`
* [Gradle](https://gradle.org/) - for building the project
* [Google Protocol Buffers](https://github.com/google/protobuf) - for use with serialization and hardware communications

### Getting started

To get started, it is best to have the latest JDK and Gradle installed. The HEAD of the `master` branch contains the latest development code and various production releases are provided on feature branches.

#### Building from the command line

Official builds are currently using with JDK 8, even though the `core` module is compatible with JDK 7 and later.

To perform a full build (*including* JavaDocs and unit/integration *tests*) use JDK 8+
```
gradle clean build
```
If you are running JDK 11 or later and Gradle 4.10 or later, the build will automatically include the JavaFX-based `wallettemplate` module. The outputs are under the `build` directory.

To perform a full build *without* unit/integration *tests* use:
```
gradle clean assemble
```

#### Building from an IDE

Alternatively, just import the project using your IDE. [IntelliJ](http://www.jetbrains.com/idea/download/) has Gradle integration built-in and has a free Community Edition. Simply use `File | New | Project from Existing Sources` and locate the `build.gradle` in the root of the cloned project source tree.

### Building and Using the Wallet Tool

The **groestlcoinj** `tools` subproject includes a command-line Wallet Tool (`wallet-tool`) that can be used to create and manage **groestlcoinj**-based wallets (both the HD keychain and SPV blockchain state.) Using `wallet-tool` on Groestlcoin's test net is a great way to learn about Groestlcoin and **groestlcoinj**.

To build an executable shell script that runs the command-line Wallet Tool, use:
```
gradle groestlcoinj-tools:installDist
```

You can now run the `wallet-tool` without parameters to get help on its operation:
```
./tools/build/install/wallet-tool/bin/wallet-tool
```

To create a test net wallet file in `~/groestlcoinj/groestlcoinj-test.wallet`, you would use:
```
mkdir ~/groestlcoinj
```
```
./tools/build/install/wallet-tool/bin/wallet-tool --net=TEST --wallet=$HOME/groestlcoinj/groestlcoinj-test.wallet create
```

To sync the newly created wallet in `~/groestlcoinj/groestlcoinj-test.wallet` with the test net, you would use:
```
./tools/build/install/wallet-tool/bin/wallet-tool --net=TEST --wallet=$HOME/groestlcoinj/groestlcoinj-test.wallet sync
```

To dump the state of the wallet in `~/groestlcoinj/groestlcoinj-test.wallet` with the test net, you would use:
```
./tools/build/install/wallet-tool/bin/wallet-tool --net=TEST --wallet=$HOME/groestlcoinj/groestlcoinj-test.wallet dump
```

Note: These instructions are for macOS/Linux, for Windows use the `tools/build/install/wallet-tool/bin/wallet-tool.bat` batch file with the equivalent Windows command-line commands and options.

### Example applications

These are found in the `examples` module.
