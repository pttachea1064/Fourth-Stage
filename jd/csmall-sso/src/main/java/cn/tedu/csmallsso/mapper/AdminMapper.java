package cn.tedu.csmallsso.mapper;

import cn.tedu.csmall.commons.pojo.admin.model.Admin;
import cn.tedu.csmall.commons.pojo.admin.vo.AdminListVO;
import cn.tedu.csmall.commons.pojo.admin.vo.AdminLoginVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 管理员Mapper接口
 */
@Mapper
public interface AdminMapper {
    //1.添加管理员
    int insert(Admin admin);
    //2.根据用户名查看该用户是否存在,统计查询数据的数量，大于等于1就是存在该用户
    int selectAdminByUserName(String userName);

    //3.查詢管理員列表
    List<AdminListVO> listAdmins();

    //4.根據用戶名稱查詢用戶名稱與密碼
    AdminLoginVO getUserByUserName(String userName);
}
