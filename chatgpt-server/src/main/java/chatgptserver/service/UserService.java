package chatgptserver.service;

import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserFeedbackPO;
import chatgptserver.bean.po.UserPO;

import java.util.List;
import java.util.Map;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */

public interface UserService {

    UserPO getUserByCode(String senderCode);

    Map<String, String> createNewChat(ChatPO chatPO);

    void chatUserFeedback(UserFeedbackRequestAO request);

    UserFeedbackListResponseAO chatUserFeedbackList(int page, int size);

    JsonResult login(UserAO request);

    String sendEmailVerifyCode(String email);
}
