package chatgptserver.controller;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesResponseAO;
import chatgptserver.bean.ao.UserAO;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/5 9:55
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册、登录")
    @PostMapping("/user/login")
    public JsonResult login(@RequestBody UserAO request) {
        log.info("UserController login request:[{}]", request);

        return JsonResult.success();
    }


    @ApiOperation("新建聊天")
    @PostMapping("/chat/add")
    public JsonResult wenXinAdd(@RequestBody ChatAddRequestAO request) {
        log.info("ChatGptController wenXinChat request:[{}]", request);
        ChatPO chatPO = ConvertMapping.ChatAddRequestAO2ChatPO(request);
        Map<String, String> response = userService.createNewChat(chatPO);

        return JsonResult.success(response);
    }

    @ApiOperation("获取聊天记录")
    @GetMapping("/chat/history")
    public JsonResult<MessagesResponseAO> historyList(@Param("chatCode") String chatCode, @Param("page") int page, @Param("size") int size) {
        log.info("ChatGptController historyList chatCode:[{}], page:[{}], size:[{}]", chatCode, page, size);
        MessagesResponseAO response = messageService.historyList(chatCode, page, size);

        return JsonResult.success(response);
    }

}
