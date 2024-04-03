package chatgptserver.bean.ao;

import chatgptserver.bean.po.UserPO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/3
 */
@Data
public class UserLoginReqAO {

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String headshot;

    @ApiModelProperty("密码")
    private String password;

    private String token;

}
