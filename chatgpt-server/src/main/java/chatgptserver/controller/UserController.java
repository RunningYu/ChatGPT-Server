package chatgptserver.controller;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.service.GptService;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private JwtUtils jwtUtils;

    @Autowired
    private GptService gptService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册、登录（首次用QQ邮箱登录，就当是注册，创建改新用户）")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/login")
    public JsonResult login(@RequestBody UserAO request) {
        log.info("UserController login request:[{}]", request);
        JsonResult response = userService.login(request);

        return response;
    }

    @ApiOperation("发邮箱验证码")
    @GetMapping("/user/send/email")
    public JsonResult sendEmailVerifyCode(@Param("email") String email) {
        log.info("UserController sendEmailVerifyCode email:[{}]", email);
        String verifyCode = userService.sendEmailVerifyCode(email);

        return JsonResult.success(verifyCode);
    }

    @ApiOperation("用户信息")
    @GetMapping("/user/info")
    public JsonResult userInfo(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        log.info("userController userInfo token:[{}]", token);
        if (token == null || "".equals(token)) {
            return JsonResult.error("请先登录");
        }
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = userService.userInfo(userCode);

        return response;
    }

    @ApiOperation("用户信息修改")
    @PostMapping("/user/info/update")
    public JsonResult userInfoUpdate(HttpServletRequest httpServletRequest,
                                     @RequestBody UserAO request) {
        log.info("userController userInfoUpdate request:[{}]", request);
        String token = httpServletRequest.getHeader("token");
        log.info("userController userInfoUpdate token:[{}]", token);
        JsonResult response = userService.userInfoUpdate(token, request);

        return response;
    }

    @ApiOperation("新建聊天")
    @PostMapping("/chat/add")
    public JsonResult chatAdd(@RequestBody ChatAddRequestAO request,
                                HttpServletRequest httpServletRequest) {
        log.info("UserController chatAdd request:[{}]", request);
        String token = httpServletRequest.getHeader("token");
        if (token == null || "".equals(token)) {
            return JsonResult.success("请先登录");
        }
        String userCode = userService.getUserCodeByToken(token);
        request.setUserCode(userCode);
        ChatPO chatPO = ConvertMapping.ChatAddRequestAO2ChatPO(request);
        Map<String, String> response = userService.createNewChat(chatPO);

        return JsonResult.success(response);
    }

    @ApiOperation("删除聊天")
    @GetMapping("/chat/delete")
    public JsonResult chatDelete(@Param("chatCode") String chatCode) {
        log.info("UserController chatDelete chatCode:[{}]", chatCode);
        userService.chatDelete(chatCode);

        return JsonResult.success("删除成功");
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
    public JsonResult<List<ChatAO>> chatCreateList(HttpServletRequest httpServletRequest, @RequestParam("gptCode") String gptCode, @RequestParam(value = "functionCode", required = false) String functionCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("UserController chatCreateList token:[{}], gptCode:[{}], functionCode:[{}]", token, gptCode, functionCode);
        List<ChatAO> response = messageService.chatCreateList(token, gptCode, functionCode);

        return JsonResult.success(response);
    }

    @ApiOperation("用户反馈")
    @PostMapping("/chat/user/feedback")
    public JsonResult chatUserFeedback(HttpServletRequest httpServletRequest, @RequestBody UserFeedbackRequestAO request) {
        log.info("UserController chatUserFeedback request:[{}]", request);
        String token = httpServletRequest.getHeader("token");
        String userCode = userService.getUserCodeByToken(token);
        if (userCode == null || "".equals(userCode)) {
            return JsonResult.error("token无效或过期");
        }
        log.info("UserController chatUserFeedback userCode:[{}]", userCode);
        request.setUserCode(userCode);
        userService.chatUserFeedback(request);

        return JsonResult.success("感谢反馈！");
    }

    @ApiOperation("用户反馈列表")
    @GetMapping("/chat/user/feedback/list")
    public JsonResult<UserFeedbackListResponseAO> chatUserFeedbackList(@Param("page") int page, @Param("size") int size) {
        log.info("UserController chatUserFeedbackList page:[{}], size:[{}]", page, size);
        UserFeedbackListResponseAO response = userService.chatUserFeedbackList(page, size);

        return JsonResult.success(response);
    }

    @ApiOperation("获取gpt平台的功能列表")
    @GetMapping("/gpt/chat/function/list")
    public JsonResult gptChatFunctionList(@Param("gptCode") String gptCode) {
        log.info("UserController gptChatFunctionList gptCode:[{}]", gptCode);
        JsonResult response = userService.gptChatFunctionList(gptCode);

        return response;
    }

    @ApiOperation("获取gpt列表")
    @GetMapping("/gpt/list")
    public JsonResult gptList() {
        log.info("UserController gptList");
        JsonResult response = gptService.gptList();

        return response;
    }

}
