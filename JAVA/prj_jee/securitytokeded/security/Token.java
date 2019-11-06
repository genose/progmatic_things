package security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import metier.Login;
import metier.User;

import java.util.Calendar;
import java.util.Date;


public class Token {

    private String token;
    private Algorithm algorithm = Algorithm.HMAC256("MySecretK3Y");

    public Token(Login login) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MINUTE, 10);
        Date expiration = calendar.getTime();
        token = JWT.create()
                .withClaim("login", login.getLogin())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    public Token(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer "))
            token = tokenHeader.substring(7);
        else token = "";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token;
    }

    public boolean isValide() {
        Boolean isCorrect = false;
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT jwt = verifier.verify(token);
            isCorrect = jwt.getExpiresAt().after(now);
        } catch (Exception e) {

        }
        return isCorrect;
    }
}
