package cn.tedu.csmall.order.webapi.mapper;

import cn.tedu.csmall.commons.pojo.order.model.OrderTb;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper {
    // 新增订单的Mapper方法
    @Insert("insert into order_tbl(user_id,commodity_code,count,money) " +
            " values(#{userId},#{commodityCode},#{count},#{money})")
    void insertOrder(OrderTb order);
}