package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */
public interface XunFeiService {

    JsonResult xfImageCreate(String content, String token, String chatCode);

    MessagesAO xfQuestion(Long threadId, QuestionRequestAO request);

    JsonResult xfImageUnderstand(Long threadId, MultipartFile file, String content, String token, String chatCode);

    JsonResult xfPptCreate(String content, String token, String chatCode);
}
