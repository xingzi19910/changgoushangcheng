package com.changgou.seckill.web.aspect;


import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//自定义切面类
@Component
@Scope
@Aspect
public class AccessLimitAop {
    @Autowired
    private HttpServletResponse httpServletResponse;
    //设置令牌的生成速率 (每秒生成2个令牌)
    private RateLimiter rateLimiter = com.google.common.util.concurrent.RateLimiter.create(2.0);
    //设置当前对哪一个注解进行增强 设置当前切点
    @Pointcut("@annotation(com.changgou.seckill.web.aspect.AccessLimit)")
    public void limit(){

    }
    @Around("limit()") //环绕增强
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        //尝试当前的访问是否能通过
        boolean flag = rateLimiter.tryAcquire();
        Object obj =null;

        if (flag){
            //允许访问
            try {
                obj=proceedingJoinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }else {
            //不允许访问 拒绝 并给用户返回提示
         String   errorMessage= JSON.toJSONString(new Result<>(false, StatusCode.ACCESSERROR,"fail"));
            //将信息返回到客户端上
                this.outMessage(httpServletResponse,errorMessage);
        }
        return obj;
    }
    //将消息返回到客户端上
    private void outMessage(HttpServletResponse httpServletResponse,String errorMessage){

        ServletOutputStream outputStream =null;

        try {
            httpServletResponse.setContentType("application/json;charset=UTF-8");
           outputStream = httpServletResponse.getOutputStream();
            outputStream.write(errorMessage.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
