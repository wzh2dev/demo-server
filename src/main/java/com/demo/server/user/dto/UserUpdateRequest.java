package com.demo.server.user.dto;

import com.demo.server.user.domain.User;
import com.demo.server.user.domain.UserStatus;

/**
 * 用户更新请求。
 */
public class UserUpdateRequest {
    private String nickname;
    private String avatar;
    private User.Gender gender;
    private UserStatus status;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public User.Gender getGender() {
        return gender;
    }

    public void setGender(User.Gender gender) {
        this.gender = gender;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
