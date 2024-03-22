package chatgptserver.Common;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */
public class SseUtils {

    public static ConcurrentHashMap<Long, SseEmitter> sseEmittersMap = new ConcurrentHashMap<>();

    public static ThreadLocal<SseEmitter> sseEmitterThreadLocal = new ThreadLocal<>();

}
