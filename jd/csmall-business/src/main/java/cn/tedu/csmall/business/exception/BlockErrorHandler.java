package cn.tedu.csmall.business.exception;

import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;
import com.alibaba.csp.sentinel.slots.block.BlockException;

//自訂限定流量類
public class BlockErrorHandler {

    //需要添加static 修飾符號 否則sentinel管理不到該類中的對應方法
    public static JsonResult blockError (StockReduceCountDTO stockReduceCountDTO,
                                  BlockException e){
        return JsonResult.failed(ResponseCode.BAD_REQUEST,"自訂限定流量類");
    }
}
