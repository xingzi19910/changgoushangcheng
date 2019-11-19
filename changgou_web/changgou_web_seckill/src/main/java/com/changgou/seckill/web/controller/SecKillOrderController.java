package com.changgou.seckill.web.controller;

import com.changgou.entity.Result;


import com.changgou.entity.StatusCode;
import com.changgou.seckill.feign.SecKillOrderFeign;
import com.changgou.seckill.web.aspect.AccessLimit;
import com.changgou.seckill.web.util.CookieUtil;
import com.changgou.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckillorder")
public class SecKillOrderController {
    @Autowired
    private SecKillOrderFeign secKillOrderFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/add")
    @AccessLimit //添加自定义限流注解
    public Result add(@RequestParam("time") String time, @RequestParam("id") Long id,String random){
        //判断随机数是否正确
        //获取cookie中的值
        String cookie = this.readCookie();
        //从redis中获取到存放的字符串
        String redisRandomCode = (String) redisTemplate.opsForValue().get("randomcode_" + cookie);
        if (StringUtils.isEmpty(redisRandomCode)){
            //如果不存在
            return new Result(false, StatusCode.ERROR,"下单失败");

        }
        //当前传递过来的random与redis中的random不匹配 返回false
        if (!random.equals(redisRandomCode)){
            return new Result(false, StatusCode.ERROR,"下单失败");
        }

        Result result = secKillOrderFeign.add(time, id);
        return  result;
    }
    @GetMapping("/getToken")
    public String getToken(){

        //生成随机数 返回字符串
        String randomString = RandomUtil.getRandomString();

        String cookieValue = this.readCookie();
        //将随机数存放在redis中   读取cookie中的jti   存放5秒
        redisTemplate.opsForValue().set("randomcode_"+cookieValue,randomString,5, TimeUnit.SECONDS);

        return randomString;
    }
    
    //读取cookie
    private String readCookie(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String cookieJti = CookieUtil.readCookie(request, "uid").get("uid");
        return cookieJti;
    }
}
