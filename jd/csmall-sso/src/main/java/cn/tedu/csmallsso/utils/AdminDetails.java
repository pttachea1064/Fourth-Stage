package cn.tedu.csmallsso.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 自訂的用戶詳細情況類
 *
 * 為了方便寫權限
 */


@Setter
@Getter
@EqualsAndHashCode
@ToString
public class AdminDetails extends User {

    private Long id ;

    public AdminDetails(String username, String password,boolean enable
            , Collection<? extends GrantedAuthority> authorities) {
        super(username, password,enable ,
                true,//帳號尚未過期
                true,//憑證尚未過期
                true,//帳號尚未被鎖定
                authorities);
    }
}
