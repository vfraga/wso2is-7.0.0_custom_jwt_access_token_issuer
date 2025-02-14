package org.wso2.sample.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.sample.constants.CustomConstants;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomClaimRequestHelper {
    private static final Logger log = LoggerFactory.getLogger(CustomClaimRequestHelper.class);

    private CustomClaimRequestHelper() {
    }

    public static Map<String, Object> getCustomClaimsFromScopes(final List<String> scopes) {
        final Map<String, Object> customClaims = new HashMap<>();

        // TODO: Ignoring hostname verification for now. Add SSL verification later with proper trust-stores.
        try (final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(getTrustAnySSLContext()))
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build()) {

            final HttpGet request = new HttpGet(CustomConstants.REQUEST_ENDPOINT);
            request.addHeader("accept", "application/json");

            try (final CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    final HttpEntity entity = response.getEntity();
                    final String result = EntityUtils.toString(entity);
                    final JSONObject jsonObject = new JSONObject(result);

                    for (final String scope : scopes) {
                        // NOTE: Case sensitive comparison.
                        if (jsonObject.has(scope)) {
                            customClaims.put(scope, jsonObject.get(scope));
                        }
                    }
                }
            }
        } catch (final IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error("Failed to retrieve custom claims from endpoint: " + CustomConstants.REQUEST_ENDPOINT, e);
        }

        return customClaims;
    }

    private static SSLContext getTrustAnySSLContext() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return SSLContexts.custom().loadTrustMaterial(null,
                        (TrustStrategy) (chain, authType) -> true)
                .build();
    }
}
