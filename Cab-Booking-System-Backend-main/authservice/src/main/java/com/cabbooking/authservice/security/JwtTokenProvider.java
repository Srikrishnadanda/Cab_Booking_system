package com.cabbooking.authservice.security;

import com.cabbooking.authservice.exception.AuthenticationAPIException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecrete;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication){
        String userName = authentication.getName();

        Date currentDate = new Date();

        Date expirationDate = new Date(currentDate.getTime()+jwtExpirationDate);

        return Jwts.builder()
                .subject(userName)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(key())
                .compact();
    }

    public Key key(){
       return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecrete));
    }

    public String getUserName(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException malformedJwtException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST,"Invalid token");
        } catch(ExpiredJwtException expiredJwtException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST,"Token Expired");
        } catch(UnsupportedJwtException unsupportedJwtException){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST,"unsupported jwt token");
        }
    }
}
