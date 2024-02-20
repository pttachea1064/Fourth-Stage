package cn.tedu.csmall.cart.webapi.service.impl;


import cn.tedu.csmall.cart.service.ICartService;
import cn.tedu.csmall.cart.webapi.mapper.CartMapper;
import cn.tedu.csmall.commons.pojo.cart.dto.CartAddDTO;
import cn.tedu.csmall.commons.pojo.cart.model.CartTb;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//@DubboService注解标记当前的业务逻辑层实现类中的所有方法都注册到Nacos中
//其他服务在订阅时会发现当前项目提供的业务逻辑层的方法，以备Dubbo消费者调用
@DubboService
@Service
@Slf4j
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public void cartAdd(CartAddDTO cartAddDTO) {
        // 当前方法参数类型是CartAddDTO,而mapper执行新增需要的是CartTb
        // 需要将参数中同名属性赋值给CartTb类型对象
        // 实例化一个CartTb类对象
        CartTb cart=new CartTb();
        // 利用工具类,将cartAddDTO对象中同名属性赋值给cart对象
        BeanUtils.copyProperties(cartAddDTO,cart);
        // 调用mapper新增购物车的方法
        cartMapper.insertCart(cart);
        // 如果新增顺利,利用log记录日志
        log.info("新增购物车商品成功!{}",cart);
    }

    @Override
    public void deleteUserCart(String userId, String commodityCode) {
        // 删除只需要直接调用mapper的删除方法即可
        cartMapper.deleteCartByUserIdAndCommodityCode(userId,commodityCode);
        log.info("购物车删除完成!");
    }
}