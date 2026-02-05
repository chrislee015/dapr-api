#!/bin/bash

# Generate SSL certificate for development
keytool -genkeypair \
    -alias gateway \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore src/main/resources/keystore.p12 \
    -validity 3650 \
    -storepass changeit \
    -keypass changeit \
    -dname "CN=localhost,OU=Development,O=Gateway,L=City,S=State,C=US" \
    -ext SAN=dns:localhost,ip:127.0.0.1

echo "SSL certificate generated successfully!"
echo "Certificate stored in: src/main/resources/keystore.p12"
echo "Password: changeit"