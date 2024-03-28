package chatgptserver.dao;

import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.UserFeedbackRequestAO;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:30
 */
@Mapper
public interface UserMapper {

    UserPO getUserByCode(@Param("userCode") String userCode);

    int newChat(ChatPO chatPO);

    void updateUserCode(@Param("chatCode") String chatCode, @Param("id") int id);

    ChatPO getChatByCode(String chatCode);

    void chatUserFeedback(UserFeedbackRequestAO request);
}
