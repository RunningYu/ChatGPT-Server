package chatgptserver.dao;

import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.MessagesPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */
@Mapper
public interface MessageMapper {

    List<MessagesPO> getWenXinHistory(@Param("chatCode") String chatCode);

    void insertMessage(MessagesPO messagesPO);

    List<MessagesPO> getHistoryList(@Param("chatCode") String chatCode, @Param("startIndex") int startIndex, @Param("size") int size);

    int getToalMessages(String chatCode);

    List<MessagesPO> getTongYiMultipleQuestionHistory(@Param("chatCode") String chatCode, @Param("id") int id);

    /**
     * 获取 通义千问：文本问答 的第一轮对话
     */
    MessagesPO getTongYiQuestionFistChat(String chatCode);

    List<ChatPO> chatCreateList(@Param("userCode") String userCode, @Param("gptCode") String gptCode, @Param("functionCode") String functionCode);

    int getChatAmount(String chatCode);

    Date getLastChatTime(String chatCode);
}
