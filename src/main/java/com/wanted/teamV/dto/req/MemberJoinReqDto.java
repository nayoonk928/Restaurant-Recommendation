package com.wanted.teamV.dto.req;

import jakarta.validation.constraints.Pattern;

public record MemberJoinReqDto(
        String account,
        @Pattern(regexp = "^(?=(.*\\d.*))(?=(.*[a-zA-Z].*))(?=(.*[!@#$%^&*()].*)).{10,}$",
                message = "비밀번호는 숫자, 문자, 특수문자 중 2가지 이상 포함하고, 최소 10자 이상이어야 합니다.")
        String password
) {
}
