package chatgptserver.bean.ao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@ApiModel("用户反馈请求体")
@Data
public class UserFeedbackAO {

    @ApiModelProperty("用户userCode")
    private String userCode;

    @ApiModelProperty("反馈内容")
    private String content;

    @ApiModelProperty("头像")
    private String headshot;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
