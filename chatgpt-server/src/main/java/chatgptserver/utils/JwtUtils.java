package chatgptserver.utils;

import chatgptserver.bean.po.UserPO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtTokenManager工具类
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/2
 */
@Component
public class JwtUtils {

    /**
     * 用于签名的私钥
     */
    private final String PRIVATE_KEY = "516Letitbe";

    /**
     * 签发者
     */
    private final String ISSUER = "Letitbe";

    /**
     * 过期时间 1 小时
     */
    private final long EXPIRATION_ONE_HOUR = 3600L;

    /**
     * 过期时间 1 月
     */
    private final long EXPIRATION_ONE_MONTH = 3600 * 24 * 30;

    /**
     * 生成Token
     * @param user         token存储的 实体类 信息
     * @param expireTime   token的过期时间
     * @return
     */
    public String createToken(UserPO user, long expireTime) {
        // 过期时间
        if ( expireTime == 0 ) {
            // 如果是0，就设置默认 1天 的过期时间
            expireTime = EXPIRATION_ONE_MONTH;
        }

        Map<String, Object> claims = new HashMap<>();
        // 自定义有效载荷部分, 将User实体类用户名和密码存储
        claims.put("userCode", user.getUserCode());
        claims.put("userame", user.getUsername());
        claims.put("password", user.getPassword());


        String token = Jwts.builder()
                // 发证人
                .setIssuer(ISSUER)
                // 有效载荷
                .setClaims(claims)
                // 设定签发时间
                .setIssuedAt(new Date())
                // 设置有效时长
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                // 使用HS512算法签名，PRIVATE_KEY为签名密钥
                .signWith(SignatureAlgorithm.HS512, PRIVATE_KEY)
                // compressWith() 压缩方法，当载荷过长时可对其进行压缩
                // 可采用jjwt实现的两种压缩方法CompressionCodecs.GZIP和CompressionCodecs.DEFLATE
                .compressWith(CompressionCodecs.GZIP)
                // 生成JWT
                .compact();
        return token;
    }

    public String createToken(UserPO user) {
        // 设置默认 1 月个 的过期时间
        long expireTime = EXPIRATION_ONE_MONTH;

        Map<String, Object> claims = new HashMap<>();
        // 自定义有效载荷部分, 将User实体类用户名和密码存储
        claims.put("userCode", user.getUserCode());
        claims.put("username", user.getUsername());
        claims.put("password", user.getPassword());

        String token = Jwts.builder()
                // 发证人
                .setIssuer(ISSUER)
                // 有效载荷
                .setClaims(claims)
                // 设定签发时间
                .setIssuedAt(new Date())
                // 设置有效时长
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                // 使用HS512算法签名，PRIVATE_KEY为签名密钥
                .signWith(SignatureAlgorithm.HS512, PRIVATE_KEY)
                // compressWith() 压缩方法，当载荷过长时可对其进行压缩
                // 可采用jjwt实现的两种压缩方法CompressionCodecs.GZIP和CompressionCodecs.DEFLATE
                .compressWith(CompressionCodecs.GZIP)
                // 生成JWT
                .compact();

        return token;
    }

    /**
     * 获取token中的User实体类
     * @param token
     * @return
     */
    public UserPO getUserFromToken(String token) {
        // 获取有效载荷
        Claims claims = getClaimsFromToken(token);
        // 解析token后，从有效载荷取出值
        String userCode = (String) claims.get("userCode");
        String username = (String) claims.get("username");
        String password = (String) claims.get("password");
        // 封装成User实体类
        UserPO user = new UserPO();
        user.setUserCode(userCode);
        user.setUsername( username );
        user.setPassword( password );

        return user;
    }
    /**
     * 获取有效载荷
     * @param token
     * @return
     */
    public Claims getClaimsFromToken(String token){
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    //设定解密私钥
                    .setSigningKey(PRIVATE_KEY)
                    //传入Token
                    .parseClaimsJws(token)
                    //获取载荷类
                    .getBody();
        }catch (Exception e){
            return null;
        }
        return claims;
    }
}
