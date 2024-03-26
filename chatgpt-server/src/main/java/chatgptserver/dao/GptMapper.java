package chatgptserver.dao;

import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.GptPO;
import chatgptserver.bean.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:30
 */
@Mapper
public interface GptMapper {

    GptPO getGptByCode(String gptCode);
}
