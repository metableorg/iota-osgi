# iota-osgi
Build OSGi bundles that export the IOTA client and wallet APIs.

Eclipse IDE and Windows platform currently supported.

# Instructions
1. Clone this repository.
2. Go to the `org.metable.iota.build.java.binding` directory.
3. `gradlew buildIota` builds the IOTA client Java binding libraries from source.
4. `gradlew copyIota` copies the `native.jar` and `iota_client.dll` files to the `org.metable.iota.client.library` bundle project and `org.metable.client.library.wint32_x86_64` bundle fragment project, respectively.
3. `gradlew buildIotaWallet` builds the IOTA wallet Java binding libraries from source.
4. `gradlew copyIotaWallet` copies the `native.jar` and `iota_wallet_java.dll` files to the `org.metable.iota.wallet.library` bundle project and `org.metable.wallet.library.wint32_x86_64` bundle fragment project, respectively.
5. Import the bundle and fragment projects into your workspace.
