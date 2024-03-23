package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */
public interface XunFeiService {

    SseEmitter xfImageUnderstand(Long threadId, String image, String question);

    JsonResult xfImageCreate(String content);
}
