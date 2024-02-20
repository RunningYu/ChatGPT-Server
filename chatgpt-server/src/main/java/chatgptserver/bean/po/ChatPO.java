package chatgptserver.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ChatPO {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "新建聊天的唯一code", required = true)
    private String chatCode;

    @ApiModelProperty(value = "用户的code", required = true)
    private String userCode;

    @ApiModelProperty(value = "新建的对话的名字", required = true)
    private String chatName;

    @ApiModelProperty(value = "1-文心一言 2-chatgpt", required = true)
    private Integer chatType;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
