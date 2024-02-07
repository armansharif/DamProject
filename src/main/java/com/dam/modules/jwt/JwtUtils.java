package com.dam.modules.jwt;


import com.dam.modules.user.controller.UserController;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtUtils {

    //    @Value("${my.jwt.expiration}")
//    private long jwtExpiration;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/verification";
    private final String SECRET = "ArmanMagnetKeyAuth8503";

    Logger logger = LoggerFactory.getLogger(UserController.class);

    public String generateToken(String username, Long id) {
        logger.info("start generateToken()");
        return Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 500 * 1000))
                .setId(id.toString())
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String getUsername(String token) {
        logger.info("start getUsername()");
        token = token.replace(TOKEN_PREFIX, "");
        String ret = "";

        ret = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
//        try { } catch (SignatureException ex) {
//            logger.info("Invalid JWT Signature");
//        } catch (MalformedJwtException ex) {
//            logger.info("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            logger.info("Expired JWT token");
//        } catch (UnsupportedJwtException ex) {
//            logger.info("Unsupported JWT exception");
//        } catch (IllegalArgumentException ex) {
//            logger.info("Jwt claims string is empty");
//        }

        return ret;
    }


    public boolean validateToken(String authToken) {
        logger.info("start validateToken()");
        authToken = authToken.replace(TOKEN_PREFIX, "");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public Long getUserId(HttpServletRequest request) {
        logger.info("start getUserId()");
        String jwt = request.getHeader(HEADER_STRING);
        if (jwt != null) {
            if (jwt.startsWith(new JwtUtils().TOKEN_PREFIX)) {
                jwt = jwt.replace(TOKEN_PREFIX, "");
                Long ret = Long.parseLong("0");
                try {
                    if (validateToken(jwt)) {
                        ret = Long.parseLong(Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwt).getBody().getId());
                    } else {
                        return null;
                    }
                } catch (SignatureException ex) {
                    logger.info("Invalid JWT Signature");
                } catch (MalformedJwtException ex) {
                    logger.info("Invalid JWT token");
                } catch (ExpiredJwtException ex) {
                    logger.info("Expired JWT token");
                } catch (UnsupportedJwtException ex) {
                    logger.info("Unsupported JWT exception");
                } catch (IllegalArgumentException ex) {
                    logger.info("Jwt claims string is empty");
                }
                return ret;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public String urlDecode(String value) {
        String decoded = value;
        try {
            decoded = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded;
    }

    public String validateMobileFormat(String mobile) {

        return mobile;
    }

    //used in Persian apps
    private static final String extendedArabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";

    //used in Arabic apps
    private static final String arabic = "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669";

    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }
}
