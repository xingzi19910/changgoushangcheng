package com.changgou.order.dao;

import com.changgou.order.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface OrderMapper extends Mapper<Order> {
    //根据当前登录人姓名查询当前用户的所有订单信息
    @Select("SELECT * FROM tb_order WHERE username = #{username} ")
    Order findOrderByUsername(@Param("username") String username);

}
