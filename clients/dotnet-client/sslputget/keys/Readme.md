# DotNetClient SSL Example

## Certs and keys
Within the DotNet framework, P12/PFX files are a standard key/cert file format.
P12/PFX files can be created with `keytool` from the keystore.
```
keytool -importkeystore -srckeystore keystore.jks -srcstoretype JKS -destkeystore client.p12 -deststoretype PKCS12
```

## Client Usage
```
CacheFactory = new CacheFactory();
CacheFactory.Set("ssl-enabled", "true");
CacheFactory.Set("ssl-keystore", KeyPath + "client.pfx");
CacheFactory.Set("ssl-keystore-password", "password");
```

## GemFire locator/server security properties:

```
ssl-enabled-components=all
ssl-keystore=keystore.jks
ssl-keystore-password=password
ssl-truststore=truststore.jks
ssl-truststore-password=password
```
