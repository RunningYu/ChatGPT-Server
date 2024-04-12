package chatgptserver.bean.ao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/8
 */
@Data
public class ChatFunctionAO {

    @ApiModelProperty("大模型code")
    private String gptCode;

    @ApiModelProperty("功能code")
    private String functionCode;

    @ApiModelProperty("功能名称")
    private String functionName;

    @ApiModelProperty("gpt名称")
    private String gptName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
