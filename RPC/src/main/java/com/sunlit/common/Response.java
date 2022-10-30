package com.sunlit.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Array;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response implements Serializable {
    //状态码
    int code;
    //状态信息
    String message;
    //返回的数据
    Object data;
    //数据类型
    Class<?> dataType;

    public static Response success(Object data) {
        return Response.builder().code(200).data(data).dataType(data.getClass()).build();
    }
    public static Response fail() {
        return Response.builder().code(500).message("服务器发生错误").build();
    }

}
