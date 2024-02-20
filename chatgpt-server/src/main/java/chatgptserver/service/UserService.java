package chatgptserver.service;

import chatgptserver.bean.po.UserPO;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */

public interface UserService {

    UserPO getUserByCode(String senderCode);
}
