package chatgptserver.bean.ao;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 10:30
 */
@ApiModel("聊天消息")
@Data
public class MessageResponseAO {

    private String studentId;

    private String username;

    private String headPicture;

    private String token;

    /** 目标接收ID（用户学号 or 群聊id） */
    private String targetId;

    private String targetUsername;

    private String targetHeadPicture;

    private String targetToken;

    private String content;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
