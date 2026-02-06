curl -X POST http://localhost:8180/realms/my-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=my-spring-api" \
  -d "client_secret=uwbAXNvQx6v0u3NSgIOHAzREqLl16aSA" \
  -d "grant_type=password" \
  -d "username=john" \
  -d "password=secret"