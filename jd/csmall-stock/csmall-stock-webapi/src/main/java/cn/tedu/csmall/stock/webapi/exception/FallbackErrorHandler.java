package cn.tedu.csmall.stock.webapi.exception;

import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class FallbackErrorHandler {

    //需要添加static 否則訪問不到該類中的對應
    public static JsonResult fallbackError(StockReduceCountDTO stockReduceCountDTO) {
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR, "自訂的降級類");

    }
}
