package chatgptserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/5/10
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisTestSet(  ) {

        ValueOperations ops = redisTemplate.opsForValue();
        ops.set( "username", 41);
        System.out.println("-----------------------------");
    }

    @Test
    public void redisTestGet(  ) {
        ValueOperations ops = redisTemplate.opsForValue();
        Object name = ops.get( "username");
        System.out.println( name );
    }

}
