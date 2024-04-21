package chatgptserver.bean.ao;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:12
 */
@ApiModel("消息返回体")
@Data
public class MessagesAO {

    @ApiModelProperty("AI的头像")
    private String chatHeadshot;

    @ApiModelProperty("用户的头像")
    private String userHeadshot;

//    @ApiModelProperty("发送方的角色：1-user 2-assistant")
//    private Integer role;

    @ApiModelProperty("发送方的唯一code")
    private String userCode;

    @ApiModelProperty("聊天框的唯一code")
    private String chatCode;

    @ApiModelProperty("发送方的名字")
    private String username;

    @ApiModelProperty("接收方的名字")
    private String chatName;

    @ApiModelProperty("图篇理解的图片url")
    private String image;

    @ApiModelProperty("问题")
    private String question;

    @ApiModelProperty("回答")
    private String replication;

    @ApiModelProperty("回答附加的照片")
    private String replyImage;

    @ApiModelProperty("0-不是预设 1-是预设")
    private Integer isDefault;

    @ApiModelProperty("问题时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("回答时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;

}
