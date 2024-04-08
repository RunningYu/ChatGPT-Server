package chatgptserver;

import chatgptserver.bean.po.UserPO;
import chatgptserver.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/8
 */

@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void test1() {
        UserPO userPO = new UserPO();
        userPO.setUserCode("123");
        userPO.setUsername("其然");
        userPO.setPassword("12345678");
        String token = jwtUtils.createToken(userPO);
        System.out.println("token: " + token);
        UserPO userPO1 = jwtUtils.getUserFromToken(token);
        System.out.println(userPO1);

    }


}
