************* Note that this is a clone of repository at https://code.google.com/p/proxy-vole/ **********
Cloned here so that we have the source when google code is retired and also can support pushing the binaries to maven central.
*********************************************************************************************************

Introduction:
-------------
The intent of this project is to build a framework to detecting the proxy settings
used by the current platform. There are multiple strategies available to detect the 
proxy settings. Proxy Vole will detect proxy settings from: 
KDE, Gnome, Windows, Firefox, Internet Explorer, ...

See java doc for usage examples and API details. 

There is also a change log in the doc folder.

New Features (Latest Version):
-------------------------------
+ **Comprehensive Proxy Selection Logging**: All proxy selectors now log at INFO level which proxy server is being used for each request. This helps clients verify that the logged proxy is reachable in their environment.
+ **JDK 21 Support**: Upgraded to JDK 21 with all dependencies updated to latest versions.
+ **Improved Code Quality**: Refactored duplicate logging code into a reusable utility method for better maintainability.

The logging feature provides detailed information including:
- Proxy host and port
- Proxy type (HTTP/SOCKS)
- Protocol context (for protocol-specific selectors)
- PAC script context (when using PAC-based proxy selection)
- Direct connection indicators

To enable logging, simply configure a logging backend using `Logger.setBackend()`.

**Example - Setting up logging:**
```java
import com.btr.proxy.util.Logger;
import com.btr.proxy.util.Logger.LogLevel;
import java.text.MessageFormat;

// Set up a console logger
Logger.setBackend(new Logger.LogBackEnd() {
    @Override
    public void log(Class<?> clazz, LogLevel loglevel, String msg, Object... params) {
        if (loglevel == LogLevel.INFO) {
            System.out.println(MessageFormat.format(msg, params));
        }
    }

    @Override
    public boolean isLogginEnabled(LogLevel logLevel) {
        return logLevel == LogLevel.INFO || logLevel == LogLevel.ERROR;
    }
});

// Now when you use proxy selectors, you'll see logs like:
// "Request to http://example.com will be sent via proxy: HTTP://proxy.example.com:8080"
// "Request to https://example.com (protocol: https) will be sent via proxy: HTTP://proxy.example.com:8080"
// "Request to http://example.com will be sent DIRECT (no proxy)"
```

Building
--------
The source can be found on the homepage at: http://code.google.com/p/proxy-vole/ 
An Eclipse project to build the source is included. 
For windows some native code is used to read the IE proxy configuration.
For the windows dll an Visual Studio project is included.
There is also an maven build file to create an JAR file for deployment.

**Requirements:**
- **JDK 21** or higher (required for building and running)
- Maven 3.x (for building)

**Building with Maven:**
```bash
mvn clean install
```

**Running Tests:**
```bash
mvn test
```

**Note:** The library has been upgraded from Java 1.5/1.6 to JDK 21. All dependencies have been updated to their latest compatible versions.

License:
--------
This project is released under BSD license (see LICENSE.txt for details).

Have fun

	- Bernd Rosstauscher
	- Rohtash Lakra
	
	
Created the branch from:
https://github.com/brsanthu/proxy-vole
