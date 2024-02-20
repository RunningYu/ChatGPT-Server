package chatgptserver.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:06
 */
@ApiModel("用户")
@Data
public class UserPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("1-已删除 0-未删除")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
