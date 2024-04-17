package chatgptserver.bean.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:06
 */
@ApiModel("用户")
@Data
public class UserAO {

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("用户输入的验证码")
    private String userVerifyCode;

    @ApiModelProperty("验证码")
    private String verifyCode;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String headShot;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("再次确认的密码")
    private String againPassword;

}
