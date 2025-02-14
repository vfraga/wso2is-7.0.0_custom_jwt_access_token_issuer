package org.wso2.sample;

import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.JWTTokenIssuer;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.sample.utils.CustomClaimRequestHelper;

import java.util.List;
import java.util.Map;

public class CustomJWTAccessTokenIssuer extends JWTTokenIssuer {
    private static final Logger log = LoggerFactory.getLogger(CustomJWTAccessTokenIssuer.class);

    public CustomJWTAccessTokenIssuer() throws IdentityOAuth2Exception {
    }

    @Override
    protected JWTClaimsSet createJWTClaimSet(final OAuthAuthzReqMessageContext authAuthzReqMessageContext,
                                             final OAuthTokenReqMessageContext tokenReqMessageContext,
                                             final String consumerKey) throws IdentityOAuth2Exception {
        final JWTClaimsSet jwtClaimSet = super.createJWTClaimSet(authAuthzReqMessageContext, tokenReqMessageContext, consumerKey);

        try {
            final String[] authzReqApprovedScopes = authAuthzReqMessageContext != null ? authAuthzReqMessageContext.getApprovedScope() : new String[0];
            final String[] tokenReqApprovedScopes = tokenReqMessageContext != null ? tokenReqMessageContext.getScope() : new String[0];

            final String[] scopes = ArrayUtils.addAll(authzReqApprovedScopes, tokenReqApprovedScopes);

            final Map<String, Object> customClaims = CustomClaimRequestHelper.getCustomClaimsFromScopes(List.of(scopes));

            log.info("Custom claims: " + customClaims);

            if (!customClaims.isEmpty()) {
                final JWTClaimsSet.Builder jwtClaimSetBuilder = new JWTClaimsSet.Builder(jwtClaimSet);
                for (final Map.Entry<String, Object> entry : customClaims.entrySet()) {
                    jwtClaimSetBuilder.claim(entry.getKey(), entry.getValue());
                }

                return jwtClaimSetBuilder.build();
            } else {
                return jwtClaimSet;
            }
        } catch (Exception e) {
            throw new IdentityOAuth2Exception("Error while creating JWT claim set", e);
        }
    }
}
