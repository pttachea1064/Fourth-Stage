package cn.tedu.csmallsso.service;

import cn.tedu.csmall.commons.pojo.admin.dto.AdminAddDTO;
import cn.tedu.csmall.commons.pojo.admin.dto.AdminLoginDTO;
import cn.tedu.csmall.commons.pojo.admin.vo.AdminListVO;

import java.util.List;

public interface IAdminService {
    //1.添加管理员
    void addAdmin(AdminAddDTO adminAddDTO);
    //2.查询管理员列表
    List<AdminListVO> listAdmins();
    //3.管理員登入
    String login(AdminLoginDTO adminLoginDTO);
}
