package com.changgou.order.controller;
import com.changgou.entity.PageResult;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.service.OrderService;
import com.changgou.order.pojo.Order;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrderService orderService;
    @Autowired
    private TokenDecode tokenDecode;



    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Order> orderList = orderService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",orderList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Order order = orderService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",order);
    }


    /***
     * 新增数据
     * @param order
     * @return
     */
    @PostMapping
    public Result<String> add(@RequestBody Order order){
        //获取登录人名称 将登录人名称保存在order中
        String username = tokenDecode.getUserInfo().get("username");
        //设置购买用户
        order.setUsername(username);

        String orderId = orderService.add(order);

        return new Result(true,StatusCode.OK,"添加成功",orderId);
    }


    /***
     * 修改数据
     * @param order
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Order order,@PathVariable String id){
        order.setId(id);
        orderService.update(order);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        orderService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Order> list = orderService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Order> pageList = orderService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 批量发货
     * @return
     */
    @PostMapping("/batchSend")
    public Result batchSend(@RequestBody List<Order> orders){
        orderService.batchSend(orders);
        return  new Result(true,StatusCode.OK,"发货成功");
    }

    /**
     * 确认收货
     */
    @PutMapping("/take/{orderId}/operator/{operator}")
    public Result take(@PathVariable String orderId,@PathVariable String operator){
        orderService.confirmTask(orderId,operator);
        return new Result(true,StatusCode.OK,"确认收货成功");
    }
        //根据当前登录人姓名查询当前用户的所有订单信息
    @GetMapping("/findOrderByUsername")
    public Result findOrderByUsername(String username){
        Order order = orderService.findOrderByUsername(username);
        return new Result(true,StatusCode.OK,"查询订单成功",order);
    }

}
