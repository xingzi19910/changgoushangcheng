package com.changgou.goods.dao;

import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {

    //扣减库存并增加销量
    @Update("UPDATE tb_sku SET num=num-#{num},sale_num=sale_num+#{num} WHERE id=#{skuId} AND num>=#{num}")
    int decrCount(OrderItem orderItem);
    @Update("UPDATE tb_sku SET num=num+#{num},sale_num=sale_num-#{num} WHERE id=#{skuId} ")
    void resumeStockNum(@Param("skuId") String skuId, @Param("num") Integer num);
}
