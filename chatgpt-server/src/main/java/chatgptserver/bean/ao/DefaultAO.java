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
 * @date : 2024/4/18
 */
@ApiModel("预设")
@Data
public class DefaultAO {

    @ApiModelProperty("预设名")
    private String name;

    @ApiModelProperty("功能code")
    private String functionCode;

    @ApiModelProperty("类型")
    private String kind;

    @ApiModelProperty("预设问题")
    private String content;

    @ApiModelProperty("预设回答")
    private String replication;

    @ApiModelProperty("预设对话条数")
    private Integer total;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
