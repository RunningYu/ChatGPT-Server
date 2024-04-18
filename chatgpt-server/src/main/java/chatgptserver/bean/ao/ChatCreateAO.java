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
public class ChatCreateAO {

    @ApiModelProperty(value = "新建聊天的唯一code", required = true)
    private String chatCode;

    @ApiModelProperty(value = "用户的code", required = false)
    private String userCode;

    @ApiModelProperty(value = "新建的对话的名字", required = true)
    private String chatName;

    @ApiModelProperty(value = "大模型code", required = true)
    private String gptCode;

    @ApiModelProperty(value = "功能code", required = true)
    private String functionCode;

    @ApiModelProperty(value = "预设问题", required = false)
    private String content;

    @ApiModelProperty(value = "预设回答", required = false)
    private String replication;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
