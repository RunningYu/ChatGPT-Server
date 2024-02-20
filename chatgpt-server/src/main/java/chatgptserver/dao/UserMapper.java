package chatgptserver.dao;

import chatgptserver.bean.ao.ChatAddRequestAO;
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

    String wenXinAdd(ChatAddRequestAO request);
}
