package chatgptserver.dao;

import chatgptserver.bean.ao.UserFeedbackRequestAO;
import chatgptserver.bean.po.ChatFunctionPO;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserFeedbackPO;
import chatgptserver.bean.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:30
 */
@Mapper
public interface UserMapper {

    UserPO getUserByCode(@Param("userCode") String userCode);

    int newChat(ChatPO chatPO);

    void updateChatCode(@Param("chatCode") String chatCode, @Param("id") int id);

    ChatPO getChatByCode(String chatCode);

    void chatUserFeedback(UserFeedbackRequestAO request);

    List<UserFeedbackPO> chatUserFeedbackList(@Param("startIndex") int startIndex, @Param("size") int size);

    int getTotalOfchatUserFeedbackList();

    UserPO getUserByEmail(String email);

    int userAdd(UserPO user);

    void updateUserCode(@Param("userCode") String userCode, @Param("id") Integer id);

    void chatDelete(String chatCode);

    List<ChatFunctionPO> gptChatFunctionList(String gptCode);

}
