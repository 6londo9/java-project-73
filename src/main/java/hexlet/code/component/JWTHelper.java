package hexlet.code.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.impl.DefaultClock;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.io.Encoders.BASE64;

@Component
public class JWTHelper {

    private final String secretKey;
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;

    public JWTHelper(@Value("${jwt.issuer:task_manager}") final String issuer,
        @Value("${jwt.expiration-sec:86400}") final Long expirationSec,
        @Value("${jwt.clock-skew-sec:300}") final Long clockSkewSec,
        @Value("${JWT_SECRET:}") final String secret) {
        this.issuer = issuer;
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = BASE64.encode(secret.getBytes(StandardCharsets.UTF_8));
        this.clock = DefaultClock.INSTANCE;
    }

    public String expiring(Map<String, Object> attributes) {
        return Jwts.builder()
                .signWith(getSignInKey(), HS256)
                .setClaims(getClaims(attributes, expirationSec))
                .compact();
    }

    public Map<String, Object> verify(final String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setClock(clock)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims getClaims(Map<String, Object> attributes, Long expirationInSec) {
        final Claims claims = Jwts.claims();
        claims.setIssuer(issuer);
        claims.setIssuedAt(clock.now());
        claims.putAll(attributes);
        if (expirationInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expirationInSec * 1000));
        }
        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

