package chatgptserver.bean.ao;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 10:32
 */
@ApiModel("通用响应体")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Data
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "结果状态", example = "200")
    private Integer code = 200;
    @ApiModelProperty(value = "结果数据")
    private T data;
    @ApiModelProperty(value = "结果描述", example = "成功")
    private String message = "ok";

    public JsonResult() {
    }

    public JsonResult(Integer code) {
        this();
        this.code = code;
    }

    public JsonResult(Integer code, T data) {
        this(code);
        this.data = data;
    }

    public JsonResult(Integer code, String message) {
        this(code);
        this.message = message;
    }

    public JsonResult(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public static <T> JsonResult<T> success() {
        return new JsonResult(200);
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult(200, data);
    }

    public static <T> JsonResult<T> success(String message, T data) {
        return new JsonResult(200, message, data);
    }

    public static <T> JsonResult<T> success(Integer code, String message, T data) {
        return new JsonResult(code, message, data);
    }

    public static JsonResult<String> error() {
        return new JsonResult(500);
    }

    public static <T> JsonResult<T> error(String message) {
        return new JsonResult(500, message);
    }

    public static <T> JsonResult<T> error(Integer code, String message) {
        return new JsonResult(code, message);
    }

    public static <T> JsonResult<T> error(Integer code, String message, T data) {
        return new JsonResult(code, message, data);
    }


}
