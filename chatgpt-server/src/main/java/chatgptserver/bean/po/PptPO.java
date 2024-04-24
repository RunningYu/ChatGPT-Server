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

    @ApiModelProperty("用户code")
    private String userCode;

    @ApiModelProperty("一级分类")
    private String firstKind;

    @ApiModelProperty("二级分类")
    private String secondKind;

    @ApiModelProperty("PPT主题")
    private String title;

    @ApiModelProperty("问题")
    private String content;

    @ApiModelProperty("PPT文件url")
    private String pptUrl;

    @ApiModelProperty("PPT封面url")
    private String coverUrl;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
