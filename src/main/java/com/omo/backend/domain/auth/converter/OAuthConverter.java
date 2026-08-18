package com.omo.backend.domain.auth.converter;

import com.omo.backend.domain.auth.dto.OAuthResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.SocialAccount;
import com.omo.backend.domain.member.enums.MemberProvider;

public class OAuthConverter {

    private static final int MEMBER_NAME_MAX_LENGTH = 20;

    // DTO -> Member 엔티티
    public static Member toGoogleMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        return Member.createGoogleMember(
                userInfo.email(),
                normalizeName(userInfo),
                null
        );
    }

    // DTO -> SocialAccount 엔티티
    public static SocialAccount toGoogleSocialAccount(Member member, OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        return SocialAccount.createSocialAccount(
                member,
                MemberProvider.GOOGLE,
                userInfo.sub(),
                userInfo.email()
        );
    }

    private static String normalizeName(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        String name = userInfo.name();
        if (name == null || name.isBlank()) {
            name = userInfo.email().substring(0, userInfo.email().indexOf('@'));
        }
        return truncate(name, MEMBER_NAME_MAX_LENGTH);
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
