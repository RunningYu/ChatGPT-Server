package chatgptserver.service;

import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.UserPO;

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

    JsonResult userInfoUpdate(String token, UserUpdateRequestAO request);

    JsonResult register(UserAO request);

    JsonResult loginByPassword(UserAO request);

    JsonResult loginByVerifyCode(UserAO request);

    JsonResult passwordForget(UserAO request);

    JsonResult loginByScan(String pid, String did, String createTime);

    JsonResult loginByScanListen(String pid, String createTime);

    JsonResult userPasswordUpdate(String userCode, String oldPassword, String newPassword, String confirmPassword);
}
