package chatgptserver.bean.ao.ppt;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:12
 */
@ApiModel("PPT响应体")
@Data
public class PptCreateResponseAO {

    @ApiModelProperty("用户的头像")
    private String userHeadshot;

    @ApiModelProperty("发送方的唯一code")
    private String userCode;

    @ApiModelProperty("发送方的名字")
    private String username;

    @ApiModelProperty("问题")
    private String question;

    @ApiModelProperty("回答")
    private String replication;

    @ApiModelProperty("PPT封面URL")
    private String coverUrl;

    @ApiModelProperty("问题时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("回答时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;

}
