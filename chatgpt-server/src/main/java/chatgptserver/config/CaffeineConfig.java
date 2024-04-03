package chatgptserver.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.annotations.ApiModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/4
 */
@ApiModel(description = "Caffeine配置")
@Configuration
public class CaffeineConfig {

    private Cache<String, Object> caffineCache;

//    static {
//        caffineCache = Caffeine.newBuilder()
//                // 初始大小
//                .initialCapacity(128)
//                // 最大数量(最大可存的key)，如果到达了阈值，就可以去做清理了，清理用的是lre策略，和redis是一样的
//                .maximumSize(1024)
//                // 过期时间（从最后一次写入开始计时）
//                .expireAfterWrite(24 * 60, TimeUnit.MINUTES)
//                .build();
//    }
//    public static Cache<String, Object> CaffeineCache() {
//        return caffineCache;
//    }

    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始大小
                .initialCapacity(128)
                // 最大数量(最大可存的key)，如果到达了阈值，就可以去做清理了，清理用的是lre策略，和redis是一样的
                .maximumSize(1024)
                // 过期时间（从最后一次写入开始计时）设置30天
                .expireAfterWrite(30, TimeUnit.DAYS)
                .build();
    }

}
