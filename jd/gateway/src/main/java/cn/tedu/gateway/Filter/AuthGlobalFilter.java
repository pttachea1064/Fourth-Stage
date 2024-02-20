package cn.tedu.gateway.Filter;


import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter (ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.從請求中獲取token
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        //2.判斷token使否為admin
        if(!StringUtils.equals(token,"admin")){
            System.out.println("鑑定權限失敗");
            //阻止向後轉發 設置回應狀態code
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            //完成回應
            return exchange.getResponse().setComplete();
        }
        //3.鑑定權限成功 繼續向後執行
        return chain.filter(exchange);
    }

    //順序 數值越小 該方法越早去執行
    @Override
    public int getOrder() {
        return 0;
    }
}
