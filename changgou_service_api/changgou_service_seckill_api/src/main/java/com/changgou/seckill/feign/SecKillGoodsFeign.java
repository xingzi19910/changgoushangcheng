package com.changgou.seckill.feign;

import com.changgou.entity.Result;
import com.changgou.seckill.pojo.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "seckill")
@RequestMapping("/seckillgoods")
public interface SecKillGoodsFeign {
    @RequestMapping("/list")
    public Result<List<SeckillGoods>> list(@RequestParam("time") String time);

}
