package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.QuestionRequestAO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */
public interface XunFeiService {

    JsonResult xfImageCreate(String content, String token, String chatCode, Boolean isRebuild, String cid);

    MessagesAO xfQuestion(Long threadId, QuestionRequestAO request);

    JsonResult xfImageUnderstand(Long threadId, MultipartFile file, String content, String token, String chatCode, Boolean isRebuild, String cid);

    JsonResult xfPptCreate(String content, String token, String chatCode, Boolean isRebuild, String cid);
}
