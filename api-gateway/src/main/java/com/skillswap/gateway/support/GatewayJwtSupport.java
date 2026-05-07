package com.skillswap.gateway.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public final class GatewayJwtSupport {

    private GatewayJwtSupport() {}

    public static SecretKey signingKey(String secret) {
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        byte[] padded = new byte[32];
        System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, 32));
        if (raw.length < 32) {
            for (int i = raw.length; i < 32; i++) {
                padded[i] = (byte) i;
            }
        }
        return Keys.hmacShaKeyFor(padded);
    }

    public static Claims parseClaims(String token, SecretKey key) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
