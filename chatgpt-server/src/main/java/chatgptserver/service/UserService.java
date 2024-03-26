package chatgptserver.service;

import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserPO;

import java.util.Map;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */

public interface UserService {

    UserPO getUserByCode(String senderCode);

    Map<String, String> createNewChat(ChatPO chatPO);
}
