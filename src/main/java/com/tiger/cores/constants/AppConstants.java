package com.tiger.cores.constants;

public final class AppConstants {

    public static final String KEY_BEARER = "Bearer ";
    public static final String SPACE = " ";
    public static final String BLANK = "";

    public static final String AUTHORIZATION = "Authorization";

    public static final String ACCEPT_LANGUAGE = "app-accept-language";

    public static final String APP_REQUEST_ID = "App-Request-Id";

    public static final String APP_REQUEST_ADDRESS = "app-request-address";

    public static final String APP_CLIENT_SITE = "app-client-site";

    public static final String MDC_CORRELATION_ID = "correlationId";

    public static final String APP_USER_AGENT = "user-agent";

    public static final String APP_TIME_ZONE = "app-time-zone";

    public static final String APP_TRANSACTION_KEY = "app-transaction-key";

    public static final String APP_TENANT_ID = "app-tenant-id";

    public static final String START = "*";

    public static final String KEY_SEPARATOR = ":";

    public static class JwtKey {
        public static final String SCOPE =
                "scope"; // refer file JwtGrantedAuthoritiesConverter, default key -> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES
        // = Arrays.asList("scope", "scp")
        public static final String TOKEN_TYPE = "token_type";
        public static final String SESSION_ID = "session_id";
        public static final String DATA = "data";
        public static final String CUSTOM_KEY_SCOPE = "scope_value";
        public static final String KEY_ROLE = "ROLE_";
        public static final String DOMAIN = "domain";
    }
}
