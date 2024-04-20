package chatgptserver.service.impl;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.DefaultAO;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.po.DefaultPO;
import chatgptserver.bean.po.GptPO;
import chatgptserver.dao.GptMapper;
import chatgptserver.service.GptService;
import chatgptserver.utils.StorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public JsonResult defaultList(String gptCode) {
        log.info("GptServiceImpl defaultList gptCode:[{}]", gptCode);
        List<DefaultPO> list = gptMapper.defaultList(gptCode);
        log.info("GptServiceImpl defaultList list:[{}]", list);
        List<DefaultAO> response = new ArrayList<>();
        for (DefaultPO defaultPO : list) {
            DefaultAO defaultAO = ConvertMapping.defaultPO2DefaultAO(defaultPO);
            defaultAO.setTotal(1);
            response.add(defaultAO);
        }

        return JsonResult.success(response);
    }

    @Override
    public JsonResult requestStop(String cid) {
        log.info("GptServiceImpl requestStop cid:[{}]", cid);
        StorageUtils.stopRequestMap.put(cid, cid);

        return JsonResult.success();
    }

}
