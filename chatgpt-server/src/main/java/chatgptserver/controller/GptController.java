package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.service.GptService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/18
 */
@Slf4j
@RestController
public class GptController {

    @Autowired
    private GptService gptService;

    @ApiOperation("预设列表")
    @GetMapping("/chat/default/list")
    public JsonResult defaultList() {
        log.info("GptController defaultList");
        JsonResult response = gptService.defaultList();

        return response;
    }

}
