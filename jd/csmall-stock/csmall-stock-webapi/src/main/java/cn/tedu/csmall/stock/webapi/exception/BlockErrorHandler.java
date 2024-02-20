package cn.tedu.csmall.stock.webapi.exception;

import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;
import com.alibaba.csp.sentinel.slots.block.BlockException;


public class BlockErrorHandler {
    //需要添加static 否則訪問不到該類中的對應
    public static JsonResult blockError(StockReduceCountDTO stockReduceCountDTO,
                                        BlockException e) {
        return JsonResult.failed(ResponseCode.BAD_REQUEST, "自訂的限制流量類");

    }
}