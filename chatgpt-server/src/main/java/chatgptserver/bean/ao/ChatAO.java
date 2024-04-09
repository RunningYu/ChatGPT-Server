package chatgptserver.bean.ao;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/5 15:01
 */
@ApiModel("新建对话请求体")
@Data
public class ChatAO {

    @ApiModelProperty(value = "新建聊天的唯一code")
    private String chatCode;

    @ApiModelProperty(value = "用户的code")
    private String userCode;

    @ApiModelProperty(value = "新建的对话的名字")
    private String chatName;

    @ApiModelProperty(value = "大模型code")
    private String gptCode;

    @ApiModelProperty(value = "功能code")
    private String functionCode;

    @ApiModelProperty(value = "聊天数量")
    private Integer chatAmount;

    @ApiModelProperty(value = "上一次聊天的时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date lastChatTime;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
