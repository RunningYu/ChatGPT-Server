package chatgptserver.controller;

import chatgptserver.bean.ao.*;
import chatgptserver.service.GptService;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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


    @ApiOperation("用户注册")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/register")
    public JsonResult register(@RequestBody UserAO request) {
        log.info("UserController register request:[{}]", request);
        JsonResult response = userService.register(request);

        return response;
    }

    @ApiOperation("扫码登录")
    @GetMapping("/user/login/by/scan")
    public JsonResult loginByScan(@RequestParam(value = "pid", required = false) String pid, @RequestParam(value = "did", required = false) String did, @RequestParam(value = "createTime", required = false) String createTime) {
        log.info("UserController loginByScan pid:[{}], did:[{}], createTime:[{}]", pid, did, createTime);
        JsonResult response = userService.loginByScan(pid, did, createTime);

        return response;
    }

    @ApiOperation("监听扫码登录")
    @GetMapping(value = "/user/login/by/scan/listen")
    public SseEmitter loginByScanListen(@RequestParam(value = "pid", required = false) String pid, @RequestParam(value = "createTime", required = false) String createTime) {
        log.info("UserController loginByScanListen pid:[{}], createTime:[{}]", pid, createTime);
        SseEmitter sseEmitter = new SseEmitter();
        JsonResult response = userService.loginByScanListen(pid, createTime);
        try {
            System.out.println("-------response-------->" + response);
            sseEmitter.send(SseEmitter.event().data(JSON.toJSONString(response)));
            sseEmitter.complete();

        } catch (IOException e) {
            throw new RuntimeException();
        }
        sseEmitter.onTimeout(() -> {

        });
        log.info("UserController loginByScanListen --> over");

        return sseEmitter;
    }

    @ApiOperation("用户快捷登录，验证码登录或注册")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/login/by/verifyCode")
    public JsonResult loginByVerifyCode(@RequestBody UserAO request) {
        log.info("UserController loginByVerifyCode request:[{}]", request);
        JsonResult response = userService.loginByVerifyCode(request);

        return response;
    }

    @ApiOperation("密码登录")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/login/by/password")
    public JsonResult loginByPassword(@RequestBody UserAO request) {
        log.info("UserController loginByPassword request:[{}]", request);
        JsonResult response = userService.loginByPassword(request);

        return response;
    }

    @ApiOperation("忘记密码，重设密码")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/password/forget")
    public JsonResult passwordForget(@RequestBody UserAO request) {
        log.info("UserController passwordForget request:[{}]", request);
        JsonResult response = userService.passwordForget(request);

        return response;
    }

    @ApiOperation("发邮箱验证码")
    @GetMapping("/user/send/email")
    public JsonResult sendEmailVerifyCode(@Param("email") String email) {
        log.info("UserController sendEmailVerifyCode email:[{}]", email);
        JsonResult response = userService.sendEmailVerifyCode(email);

        return response;
    }

    @ApiOperation("用户信息")
    @GetMapping("/user/info")
    public JsonResult userInfo(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        log.info("userController userInfo token:[{}]", token);
        if (token == null || "".equals(token)) {
            return JsonResult.error(401, "请先登录");
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

    @ApiOperation("用户密码修改")
    @GetMapping("/user/password/update")
    public JsonResult userPasswordUpdate(HttpServletRequest httpServletRequest,
                                     @RequestParam(value = "oldPassword", required = true) String oldPassword,
                                     @RequestParam(value = "newPassword", required = true) String newPassword,
                                     @RequestParam(value = "confirmPassword", required = true) String confirmPassword) {


        log.info("userController userPasswordUpdate oldPassword:[{}], newPassword:[{}], confirmPassword:[{}]", oldPassword, newPassword, confirmPassword);
        String token = httpServletRequest.getHeader("token");
        log.info("userController userPasswordUpdate token:[{}]", token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = userService.userPasswordUpdate(userCode, oldPassword, newPassword, confirmPassword);

        return response;
    }

    @ApiOperation("新建聊天")
    @PostMapping("/chat/add")
    public JsonResult chatAdd(@RequestBody ChatAddRequestAO request,
                                HttpServletRequest httpServletRequest) {
        log.info("UserController chatAdd request:[{}]", request);
        String token = httpServletRequest.getHeader("token");
        if (token == null || "".equals(token)) {
            return JsonResult.error(401, "如果想尝试更多的功能，请先注册活登录");
        }
        String userCode = userService.getUserCodeByToken(token);
        if (userCode == null) {
            return JsonResult.error(401, "token过期");
        }
        request.setUserCode(userCode);
        Map<String, String> response = userService.createNewChat(request);

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
        if (gptCode == null || "".equals(gptCode)) {
            return JsonResult.error("请选定 gptCode");
        }
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
            return JsonResult.error(401, "如果想尝试更多的功能，请先注册活登录");
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
