# iota-osgi
Build OSGi bundles that expose the IOTA client and wallet APIs.

# Build Java Bindings
1. Clone https://github.com/iotaledger/iota.rs.git
2. Use these instructions as a guide [Getting Started with Java](https://wiki.iota.org/iota.rs/libraries/java/getting_started).
3. Go to the /path/to/iota.rs/bindings/java/ directory and perform a gradle build i.e., enter "gradle build". This builds the .jar file that contains the java binding to the native iota-client library.
4. Go to the /path/to/iota.rs/bindings/java/ directory and perform "cargo build --release" to build the native library (i.e., .dll for windows .so for linux).
