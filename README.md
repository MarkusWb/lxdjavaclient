# lxd linuxcontainer Java Client

Stump embedded Jetty Java client for passing through JSON requests/responses between
 [lxd API](https://linuxcontainers.org/lxd/docs/master/rest-api) and JS web apps.

Connects to remote lxd Client REST API by HTTPS, using client certificate authentication.

## Generate key file

openssl genrsa 2048 > lxc.key

## Extract certificate file

openssl req -new -x509 -nodes -sha256 -key lxc.key -out lxc.cert

## Convert to PKCS12 file

openssl pkcs12 -export -inkey lxc.key -in lxc.cert -out lxc.p12

## Add Client to trust
Copy the `lxc.cert` file to the server and add it by

`lxc config trust add lxc.cert`

## Run lxdjavaclient
Build artifact with Gradle task fatJar.

Put keystore file in same directory as JAR file.

Call

`java -jar lxdjavaclient-all-1.0-SNAPSHOT.jar serverIP keyStorePwd`

The Jetty server listens on port 4567, call paths like for the
 [LXD API](https://linuxcontainers.org/lxd/docs/master/rest-api#api-structure), without the leading **/1.0**

Only GET request are supported 