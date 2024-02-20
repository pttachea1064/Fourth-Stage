package cn.tedu.csmallsso.controller;

import cn.tedu.csmall.commons.pojo.admin.dto.AdminAddDTO;
import cn.tedu.csmall.commons.pojo.admin.dto.AdminLoginDTO;
import cn.tedu.csmall.commons.pojo.admin.vo.AdminListVO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmallsso.service.IAdminService;
import cn.tedu.csmallsso.utils.LoginPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@Api(tags= "管理員的管理模型")
@Slf4j
public class AdminController {


    @Autowired
    private IAdminService adminService;

    @ApiOperation("添加管理人員的操作")
    @PostMapping("/add-new")
    @PreAuthorize("hasAuthority('/ams/admin/update')")
    public JsonResult addNew(@RequestBody AdminAddDTO adminAddDTO){
        adminService.addAdmin(adminAddDTO);
        return JsonResult.ok("添加管理員成功");
    }

    @GetMapping("/list")
    @ApiOperation("查询管理员列表")
    //@PreAuthorize 用于获取当前用户的认证信息，来检查是否有权限操作该方法
    @PreAuthorize("hasAuthority('/ams/admin/read')")
    //@AuthenticationPrincipal用于获取当前用户的认证信息，并注入到修饰的参数中
    public JsonResult<List<AdminListVO>> getListAdmins(@AuthenticationPrincipal LoginPrincipal loginPrincipal){
        List<AdminListVO> adminListVOS = adminService.listAdmins();
        return JsonResult.ok("查询管理员列表成功",adminListVOS);
    }
    @PostMapping("/login")
    @ApiOperation("管理員登入")
    public JsonResult login(@RequestBody AdminLoginDTO adminLoginDTO){
        log.info("登入要進行的處理");
        String jwt = adminService.login(adminLoginDTO);
        return JsonResult.ok(jwt);
    }
}
