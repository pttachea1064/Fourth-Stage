package cn.tedu.csmallsso.mapper;

import cn.tedu.csmall.commons.pojo.admin.model.Admin_Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminRoleMapper {
    //1.插入管理員與角色的關聯數據
    int insert (Admin_Role adminRole);
}
