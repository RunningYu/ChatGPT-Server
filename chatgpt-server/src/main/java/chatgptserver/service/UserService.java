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

    Map<String, String> createNewChat(ChatAddRequestAO request);

    void chatUserFeedback(UserFeedbackRequestAO request);

    UserFeedbackListResponseAO chatUserFeedbackList(int page, int size);

    JsonResult sendEmailVerifyCode(String email);

    void chatDelete(String chatCode);

    String getUserCodeByToken(String token);

    JsonResult gptChatFunctionList(String gptCode);

    JsonResult userInfo(String userCode);

    JsonResult userInfoUpdate(String token, UserAO request);

    JsonResult register(UserAO request);

    JsonResult loginByPassword(UserAO request);

    JsonResult loginByVerifyCode(UserAO request);

    JsonResult passwordForget(UserAO request);
}
