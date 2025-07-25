package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.RegexUtils.isPhoneInvalid;

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
}
