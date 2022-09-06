package com.troublemaker.clockin.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.troublemaker.clockin.entity.ResultEnum;
import com.troublemaker.clockin.exception.BizException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.config
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  22:19
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data.jwt")
public class JWTTokenConfiguration {

    private String header;     // 定义token返回头部
    private String tokenPrefix;     // token前缀

    private String alg;     // 加密方式
    private String secret;    // 签名密钥
    private long expireTime;     // 有效期

    /**
     * @description: 创建token
     * @author: troublemaker
     * @date: 20:02
     * @param: [user]
     * @return: java.lang.String
     **/
    public Map<String, String> createToken(String userId) {
        // 转换为分钟
        expireTime *= 1000 * 60;
        Map<String, Object> map = new HashMap<>();
        map.put("typ", "JWT");
        map.put("alg", "HS256");
        String token = tokenPrefix +
                JWT.create()
                        .withHeader(map)  // header
                        .withIssuer("Troublemaker")// 签名是有谁生成
                        .withIssuedAt(new Date()) // 签发时间
                        .withExpiresAt(new Date(System.currentTimeMillis() + expireTime)) // 过期时间
                        .withClaim("userID", userId) // 自定义信息 userID
                        .sign(Algorithm.HMAC256(secret));// 根据密钥加密 header和 payload
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(header,token);
        return hashMap;

    }


    /**
     * @description: 验证 token
     * @author: troublemaker
     * @date: 20:02
     * @param: [token]
     * @return: void
     **/
    public Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt;
        try {
            // 去除前缀
            token = token.substring(tokenPrefix.length());
            // 构建密钥信息
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 通过密钥信息和签名的发布者的信息生成JWTVerifier(JWT验证类)。不提那家发布者的信息也可以获取JWTVerifier
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Troublemaker")  // 不添加.withIssuer("Troublemaker") 也可以获取JWTVerifier
                    .build();
            // 通过JWTVerifier获取token中的信息
            jwt = verifier.verify(token);
        } catch (IllegalArgumentException | JWTVerificationException e) {
            throw new BizException(ResultEnum.SIGNATURE_NOT_MATCH);
        }
        return jwt.getClaims();
    }
}

