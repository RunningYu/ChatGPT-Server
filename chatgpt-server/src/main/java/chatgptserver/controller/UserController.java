package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.UserAO;
import chatgptserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/5 9:55
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册、登录")
    @PostMapping("/user/login")
    public JsonResult login(@RequestBody UserAO request) {
        log.info("UserController login request:[{}]", request);

        return JsonResult.success();
    }

}
