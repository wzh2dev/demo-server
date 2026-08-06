package com.demo.server.user.vo;

import com.demo.server.user.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户 Entity ↔ VO 转换器。
 */
public final class UserConverter {

    private UserConverter() {
    }

    public static UserVO toVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setMobile(user.getMobile());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setStatus(user.getStatus() == null ? null : user.getStatus().name());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setUpdatedAt(user.getUpdatedAt());
        return vo;
    }

    public static List<UserVO> toVOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            list.add(toVO(user));
        }
        return list;
    }
}
