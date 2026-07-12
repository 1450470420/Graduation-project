package com.xypt.controller.app;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.UserAddress;
import com.xypt.service.UserAddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
/** 用户常用地址管理接口 */
@RestController
@RequestMapping("/api/app/addresses")
public class AppAddressController {
    @Autowired UserAddressService addressService;
    @GetMapping
    public Result<List<UserAddress>> list(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(addressService.list(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId).orderByDesc(UserAddress::getIsDefault)));
    }
    @PostMapping
    public Result<String> add(@RequestBody UserAddress address, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        address.setUserId(userId); address.setCreateTime(LocalDateTime.now());
        if (address.getIsDefault()!=null&&address.getIsDefault()==1) {
            addressService.list(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId)).forEach(a -> { a.setIsDefault(0); addressService.updateById(a); });
        }
        addressService.save(address);
        return Result.success("添加成功");
    }
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody UserAddress address, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        address.setId(id); address.setUserId(userId);
        if (address.getIsDefault()!=null&&address.getIsDefault()==1) {
            addressService.list(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId)).forEach(a -> { a.setIsDefault(0); addressService.updateById(a); });
        }
        addressService.updateById(address);
        return Result.success("更新成功");
    }
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        addressService.removeById(id);
        return Result.success("删除成功");
    }
}
