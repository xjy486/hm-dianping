package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.RegexUtils.isPhoneInvalid;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1. 校验手机号
        if(isPhoneInvalid(phone)) {
            // 2，如果不符合，返回错误信息
            return Result.fail("手机号格式错误");
        }

        //  3. 如果符合，生成验证码
        String code = RandomUtil.randomNumbers(6); // 生成6位随机数字验证码
        // 4. 保存验证码到session中，方便后续登录时校验
        session.setAttribute("code", code);
        // 5. 调用短信服务发送验证码
        // TODO 没钱，不做了
        log.debug("发送短信验证码成功，手机号：{}，验证码：{}", phone, code);
        // 返回ok
        return Result.ok();
    }
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session){
        // 1.校验手机号和验证码
        String phone = loginForm.getPhone();
        if (isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
        Object code = session.getAttribute("code");
        if( code == null || !code.toString().equals(loginForm.getCode())) {
            // 2，不一致直接报错
            return Result.fail("验证码错误");
        }
        // 3.一致，查看数据库中是否有该手机号的用户 select * from tb_user where phone = ?
        User user = lambdaQuery().eq(User::getPhone, phone).one();
        // 4.如果没有，创建新用户
        if(user == null){
           user = createUserWithPhone(phone);
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        // 5.如果有，保存用户信息到session,直接登录
        session.setAttribute("user",userDTO);
        return Result.ok();
    }
    @Override
    public Result me() {
        // 1.从session中获取用户信息
        UserDTO userDTO = (UserDTO) UserHolder.getUser();
        User user = lambdaQuery()
                .eq(User::getId, userDTO.getId())
                .one();
        // 2.返回用户信息
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(userDTO);
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        // 生成随机昵称
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 设置默认头像
        user.setIcon("https://example.com/default-avatar.png");
        // 保存用户到数据库
        save(user);
        return user;
    }
}
