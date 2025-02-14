## Custom JWT Token Issuer (IS 7.0.0)

An extended implementation of the JWT token issuer to pull claims from an external source and add claims based on the `scope` value.

### Steps
1. Implement and build the sample:

   - Create a maven project with a custom class extending the `JWTTokenIssuer` class, and override the `accessToken` methods.
   - Add the necessary dependencies in the `pom.xml`.
   - Add the JWT claims by using the `JWTClaimsSet.Builder` instance.
   - Build the Maven project (e.g., `mvn clean install`).
   - Copy the JAR file from the `<PROJECT_HOME>/target` folder to the `<IS_HOME>/repository/components/lib` directory.
[README.md](../../Desktop/wso2_custom_jwt_access_token_issuer/README.md)

2. Add the configuration below in the `<IS_HOME>/repository/conf/deployment.toml` file:
   
    ```toml
    [oauth.extensions.token_types.token_type]
    name="Custom_JWT"
    issuer="org.wso2.sample.CustomJWTAccessTokenIssuer"
    persist_access_token_alias=false
    ```
    * _Note on `name`: Its value will be shown in the Service Provider's under 'Token Types'._
    * _Note on `issuer`: Replace `org.wso2.sample.CustomJWTAccessTokenIssuer` with your custom class name._
    * _Note on `persist_access_token_alias`: This property is set to `false` to avoid persisting the access token alias (`jti` field for JWT access tokens) as the access token value in the cache and database._


3. Restart the WSO2 Identity Server.

