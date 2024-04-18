package chatgptserver.service.impl;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.po.DefaultPO;
import chatgptserver.bean.po.GptPO;
import chatgptserver.dao.GptMapper;
import chatgptserver.service.GptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/25
 */
@Slf4j
@Service
public class GptServiceImpl implements GptService {

    @Autowired
    private GptMapper gptMapper;

    @Override
    public JsonResult gptList() {
        List<GptPO> list = gptMapper.gptList();
        log.info("GptServiceImpl gptList list:[{}]", list);

        return JsonResult.success(list);
    }

    @Override
    public JsonResult defaultList() {
        log.info("GptServiceImpl defaultList");
        List<DefaultPO> list = gptMapper.defaultList();
        log.info("GptServiceImpl defaultList list:[{}]", list);

        return JsonResult.success(list);
    }

}
