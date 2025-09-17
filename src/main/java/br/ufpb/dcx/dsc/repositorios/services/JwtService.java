package br.ufpb.dcx.dsc.repositorios.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret_key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private Long expiration;

    public String generateToken(UserDetails userDetails){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+expiration))
                .sign(algorithm);
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try{
            String username = extractUserName(token);
            return username.equals(userDetails.getUsername());
        } catch (JWTVerificationException e){
            return false;
        }
    }

    public String extractUserName(String token) {
        DecodedJWT decodedJWT = verifyAndDecodeToken(token);
        return decodedJWT.getSubject();
    }

    public DecodedJWT verifyAndDecodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
