package cn.tedu.csmall.business.exception;

import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;

//自定义的降级类
public class FallbackErrorHandler {

    //需要添加static修饰符，否则sentinel管理不到该类中的对用的方法
    public  static JsonResult fallbackError(StockReduceCountDTO stockReduceCountDTO){
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,"自定义降级类");
    }
}
