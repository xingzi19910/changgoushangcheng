package com.changgou.system.filter;

import com.changgou.system.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //2.获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //3.判断当前的请求是否为登录请求,如果是,则直接放行
        if (request.getURI().getPath().contains("/admin/login")){
            //放行
            return  chain.filter(exchange);
        }
        //4.获取当前的所有请求头信息
        HttpHeaders headers = request.getHeaders();
        //5.获取jwt令牌信息
        String token = headers.getFirst("token");
        //6.判断当前令牌是否存在,如果不存在,则向客户端范围错误提示信息
        if (StringUtils.isEmpty(token)){
            //如果用户不存在 则向客户端返回错误提示信息
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
          //6.1如果令牌存在,解析jwt令牌,判断该令牌是否合法 如果不合法则向客户端返回错误提示信息


        try {
            //解析令牌
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            //令牌解析失败  向客户端返回错误信息
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        //6.2如果令牌合法,则放行

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
