package chatgptserver.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/24
 */
@Data
@ApiModel("PPT上传请求体")
public class PptPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("PPT的code")
    private String pptCode;

    @ApiModelProperty("用户code")
    private String userCode;

    @ApiModelProperty("用户c用户名称ode")
    private String username;

    @ApiModelProperty("一级分类")
    private String firstKind;

    @ApiModelProperty("二级分类")
    private String secondKind;

    @ApiModelProperty("PPT主题")
    private String title;

    @ApiModelProperty("问题")
    private String content;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("PPT封面url")
    private String coverUrl;

    @ApiModelProperty("PPT文件url")
    private String pptUrl;

    @ApiModelProperty("评分")
    private Double score;

    @ApiModelProperty("收藏量")
    private Integer collectAmount;

    @ApiModelProperty("浏览量")
    private Integer seeAmount;

    @ApiModelProperty("评论量")
    private Integer commentAmount;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
