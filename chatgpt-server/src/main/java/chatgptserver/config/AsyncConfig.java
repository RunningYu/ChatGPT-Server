package chatgptserver.config;


import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/17
 */
@ApiModel(description = "线程池配置")
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        //线程池设置
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程
        taskExecutor.setCorePoolSize( 8 );
        //最大线程
        taskExecutor.setMaxPoolSize( 16 );
        //队列大小
        taskExecutor.setQueueCapacity( 64 );
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix( "async-token-" );
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SpringAsyncExceptionHandler();
    }

    class SpringAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
            logger.error("Exception occurs in async method", throwable.getMessage());
        }
    }
}