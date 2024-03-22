package chatgptserver.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */
public interface XunFeiService {

//    JsonResult xfImageUnderstand(SseEmitter sseEmitter, String image);
    SseEmitter xfImageUnderstand(Long threadId, String image);

}
