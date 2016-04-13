# Fixing SSL Certificate issue on production

We post PubSub notifications to the We Are Curious endpoint whenever user synchronizes their ring data to the Ouracloud.
Since the notification URL of We Are Curious is SSL based, the Groovy HTTP client may fail to connect to that URL due
to some SSL issues. The following error may be thrown:

```
sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

To fix this issue, we need to add the certificate of We Are Curious to the Java certificates. To do this, first compile
the `InstallCert.java` file.

```
javac InstallCert.java
```

Now, download ther certificate of We Are Curious: (Press enter when ask to enter certificate)

```
java InstallCert wearecurio.us:443
```

This will generate a file `jssecacerts` in the same directory where you have ran the java command. Now, copy the `jssecacerts`
to `$JAVA_HOME/jre/lib/security` and restart the container i.e. Jetty (if on production) or Tomcat (if running locally).

Read more at http://stackoverflow.com/a/30532709/2405040
