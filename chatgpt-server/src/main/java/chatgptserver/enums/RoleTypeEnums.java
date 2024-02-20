package chatgptserver.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Getter
@AllArgsConstructor
public enum RoleTypeEnums {

    /** 文心一言 */
    WEN_XIN_USER(1, "user"),
    WEN_XIN_ASSISTANT(2, "assistant"),

    /** 错误 */
    ERROR(-1, "不存在该角色类型");

    private Integer type;

    private String role;

    public static String getRole(Integer type) {
        for (RoleTypeEnums value : RoleTypeEnums.values()) {
            if (value.getType().equals(type)) {
                return value.role;
            }
        }
        return ERROR.role;
    }
}
