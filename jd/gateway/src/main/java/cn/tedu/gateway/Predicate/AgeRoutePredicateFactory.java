package cn.tedu.gateway.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component //將Bean管理的權限交給Spring管理
public class AgeRoutePredicateFactory extends
        AbstractRoutePredicateFactory<AgeRoutePredicateFactory.Config> {

    public AgeRoutePredicateFactory(){
        super(AgeRoutePredicateFactory.Config.class);
    }

    //綁定靜態內部類 用於從配置文件中接收參數屬性
    @Override
    public List<String> shortcutFieldOrder(){
        //這裡面是用數組來表示 所以順序要跟配置文件中的屬性順序一致
        return Arrays.asList("minAge","maxAge");
    }

    //斷言Predicate邏輯代碼
    @Override
    public Predicate<ServerWebExchange> apply(AgeRoutePredicateFactory.Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                //可以接收前台傳入的參數
                String ageKey = serverWebExchange.getRequest().getQueryParams().getFirst("age");

                //判斷age是否為空
                if(StringUtils.isNotEmpty(ageKey)){
                    //age不為空,獲取age對應的數值
                    //判斷age的數值是否再配置文件指定的範圍中,在指定範圍內返回true
                    int ageValue = Integer.parseInt(ageKey);
                    if (ageValue<config.getMaxAge()&&ageValue>config.getMinAge())
                        return true;
                    //不再範圍內 返回false
                    return false;
                }
                return false;
            }
        };
    }

    //靜態內部類 主要是接收配置文件中的訊息
    public static class Config{
        private int minAge;
        private int maxAge;

        public Config(){

        }

        public int getMinAge() {
            return minAge;
        }

        public void setMinAge(int minAge) {
            this.minAge = minAge;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }


}
