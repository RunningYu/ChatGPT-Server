package chatgptserver.bean.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:06
 */
@ApiModel("用户")
@Data
public class UserUpdateRequestAO {

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private MultipartFile headShot;

    @ApiModelProperty("密码")
    private String password;

    public UserUpdateRequestAO(MultipartFile headShot, String email, String username) {
        this.headShot = headShot;
        this.email = email;
        this.username = username;
    }
}
