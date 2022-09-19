package com.lwq.reggie.common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLIntegrityConstraintViolationException;
//全局的异常处理
//指的是拦截哪些controller
@ControllerAdvice(annotations = {RestController.class})
@Slf4j
@ResponseBody
//通过走数据流将数据封装成json数据
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> Handler1(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            //如果是重复的用户名造成异常
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        String msg = "未知错误";
        //给前端显示失败信息
        return R.error(msg);
    }

    @ExceptionHandler(CustomException.class)
    public R<String> Handler2(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
