package chatgptserver.bean.ao.ppt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/24
 */
@Data
@ApiModel("PPT上传请求体")
public class PptUploadRequestAO {

    @ApiModelProperty("PPT文件")
    @NotNull(message = "PPT文件不能为空")
    private MultipartFile pptFile;

    @ApiModelProperty("PPT封面")
    @NotNull(message = "PPT封面不能为空")
    private MultipartFile pptCoverFile;

    @ApiModelProperty("一级分类")
    @NotBlank(message = "一级分类firstKind不能为空")
    private String firstKind;

    @ApiModelProperty("二级分类")
    private String secondKind;

    @ApiModelProperty("PPT主题")
    @NotBlank(message = "PPT主题title不能为空")
    private String title;

    @ApiModelProperty("用户code")
    private String userCode;

}
