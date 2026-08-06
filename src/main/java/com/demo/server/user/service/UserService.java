package com.demo.server.user.service;

import com.demo.server.user.domain.Address;
import com.demo.server.user.domain.User;
import com.demo.server.user.domain.UserStatus;
import com.demo.server.user.dto.PageResult;
import com.demo.server.user.dto.UserCreateRequest;
import com.demo.server.user.dto.UserUpdateRequest;
import com.demo.server.user.exception.ConflictException;
import com.demo.server.user.exception.NotFoundException;
import com.demo.server.user.exception.ValidationException;
import com.demo.server.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 用户领域服务。
 */
public class UserService {

    /**
     * 中国大陆 11 位手机号校验。
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 简化版邮箱校验。
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final int NICKNAME_MAX_LEN = 32;
    private static final int PASSWORD_MIN_LEN = 8;
    private static final int PASSWORD_MAX_LEN = 64;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    /**
     * 创建用户。
     *
     * @param req 创建请求
     * @return 已创建用户
     */
    public User create(UserCreateRequest req) {
        validateCreateRequest(req);
        if (userRepository.findByMobile(req.getMobile()) != null) {
            throw new ConflictException("mobile already exists: " + req.getMobile());
        }
        User user = new User();
        user.setNickname(req.getNickname());
        user.setMobile(req.getMobile());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setGender(req.getGender() == null ? User.Gender.UNKNOWN : req.getGender());
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    /**
     * 更新用户。
     */
    public User update(Long id, UserUpdateRequest req) {
        User user = requireUser(id);
        if (req.getNickname() != null) {
            if (req.getNickname().length() > NICKNAME_MAX_LEN) {
                throw new ValidationException("nickname too long");
            }
            user.setNickname(req.getNickname());
        }
        if (req.getAvatar() != null) {
            user.setAvatar(req.getAvatar());
        }
        if (req.getGender() != null) {
            user.setGender(req.getGender());
        }
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }
        return userRepository.save(user);
    }

    /**
     * 查询用户详情。
     */
    public User get(Long id) {
        return requireUser(id);
    }

    /**
     * 删除用户。
     */
    public void delete(Long id) {
        if (!userRepository.deleteById(id)) {
            throw new NotFoundException("user", id);
        }
    }

    /**
     * 分页查询用户。
     */
    public PageResult<User> list(String keyword, UserStatus status, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        List<User> items = userRepository.findAll(keyword, status, pageNumber, pageSize);
        long total = userRepository.count(keyword, status);
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    /**
     * 校验用户登录，仅用于演示。
     */
    public User login(String mobile, String password) {
        User user = userRepository.findByMobile(mobile);
        if (user == null) {
            throw new NotFoundException("user not found by mobile: " + mobile);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ValidationException("user not active: " + user.getStatus());
        }
        if (!Objects.equals(user.getPassword(), password)) {
            throw new ValidationException("password mismatch");
        }
        return user;
    }

    private User requireUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException("user", id);
        }
        return user;
    }

    private void validateCreateRequest(UserCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getNickname() == null || req.getNickname().isEmpty()) {
            throw new ValidationException("nickname is empty");
        }
        if (req.getNickname().length() > NICKNAME_MAX_LEN) {
            throw new ValidationException("nickname too long");
        }
        if (req.getMobile() == null || !MOBILE_PATTERN.matcher(req.getMobile()).matches()) {
            throw new ValidationException("invalid mobile: " + req.getMobile());
        }
        if (req.getEmail() != null && !req.getEmail().isEmpty()
                && !EMAIL_PATTERN.matcher(req.getEmail()).matches()) {
            throw new ValidationException("invalid email: " + req.getEmail());
        }
        if (req.getPassword() == null
                || req.getPassword().length() < PASSWORD_MIN_LEN
                || req.getPassword().length() > PASSWORD_MAX_LEN) {
            throw new ValidationException("password length must be " + PASSWORD_MIN_LEN + "-" + PASSWORD_MAX_LEN);
        }
    }

    /**
     * 演示：返回用户所有地址占位列表（实际应注入 AddressRepository）。
     */
    public List<Address> listAddresses(Long userId) {
        requireUser(userId);
        return new ArrayList<>();
    }
}
