package chatgptserver.dao;

import chatgptserver.bean.po.MessagesPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
