package chatgptserver.controller;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserFeedbackPO;
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
        log.info("UserController wenXinChat request:[{}]", request);
        ChatPO chatPO = ConvertMapping.ChatAddRequestAO2ChatPO(request);
        Map<String, String> response = userService.createNewChat(chatPO);

        return JsonResult.success(response);
    }

    @ApiOperation("获取聊天记录")
    @GetMapping("/chat/history")
    public JsonResult<MessagesResponseAO> historyList(@Param("chatCode") String chatCode, @Param("page") int page, @Param("size") int size) {
        log.info("UserController historyList chatCode:[{}], page:[{}], size:[{}]", chatCode, page, size);
        MessagesResponseAO response = messageService.historyList(chatCode, page, size);

        return JsonResult.success(response);
    }

    @ApiOperation("获取用户创建的聊天列表")
    @GetMapping("/chat/create/list")
    public JsonResult<List<ChatPO>> chatCreateList(@Param("userCode") String userCode, @Param("gptCode") String gptCode) {
        log.info("UserController chatBoxList userCode:[{}], gptCode:[{}]", userCode, gptCode);
        List<ChatPO> response = messageService.chatCreateList(userCode, gptCode);

        return JsonResult.success(response);
    }

    @ApiOperation("用户反馈")
    @PostMapping("/chat/user/feedback")
    public JsonResult chatUserFeedback(@RequestBody UserFeedbackRequestAO request) {
        log.info("UserController chatUserFeedback request:[{}]", request);
        userService.chatUserFeedback(request);

        return JsonResult.success();
    }

    @ApiOperation("用户反馈列表")
    @GetMapping("/chat/user/feedback/list")
    public JsonResult<UserFeedbackListResponseAO> chatUserFeedbackList(@Param("page") int page, @Param("size") int size) {
        log.info("UserController chatUserFeedbackList page:[{}], size:[{}]", page, size);
        UserFeedbackListResponseAO response = userService.chatUserFeedbackList(page, size);

        return JsonResult.success(response);
    }

}
