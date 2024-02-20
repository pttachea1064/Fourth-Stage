package cn.tedu.gateway.Filter;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<LogGatewayFilterFactory.Config> {

    //構造函數
    public LogGatewayFilterFactory(){
        super(Config.class);
    }

    //綁定靜態內部類
    @Override
    public List<String> shortcutFieldOrder(){
        return Arrays.asList("consoleLog","cacheLog");
    }

    //过滤器的逻辑判断
    @Override
    public GatewayFilter apply(Config config){
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                if(config.isConsolelog()){
                    System.out.println("consoleLog已经开启......");
                }
                if(config.isCachelog()){
                    System.out.println("cacheLog已经开启......");
                }
                return chain.filter(exchange);
            }
        };
    }

    //靜態內部類 用來接收文件的參數
    public static class Config{
        private boolean consolelog;
        private boolean cachelog;

        //下方的內容由於是判斷 所以Setter就好 Get我們用Boolean類型來寫
        public boolean isConsolelog(){
            return consolelog;
        }

        public void setConsolelog(boolean consolelog) {
            this.consolelog = consolelog;
        }

        public boolean isCachelog(){
            return cachelog;
        }

        public void setCachelog(boolean cachelog) {
            this.cachelog = cachelog;
        }
    }
}
