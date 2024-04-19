package chatgptserver.dao;

import chatgptserver.bean.po.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:30
 */
@Mapper
public interface GptMapper {

    GptPO getGptByCode(String gptCode);

    List<GptPO> gptList();

    List<DefaultPO> defaultList(String gptCode);

    String getFunctionNameInfo(String functionCode);
}
