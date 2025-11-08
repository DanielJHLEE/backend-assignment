package com.allra.backend.global.validator;

import com.allra.backend.global.exception.BusinessException;

/**
 * 공통 권한 검증 유틸리티
 * - 엔티티의 userId와 요청 userId 비교
 */
public final class AuthValidator {

    private AuthValidator() {}

    public static void validateOwnership(Long entityUserId, Long requestUserId, String targetName) {
        if (!entityUserId.equals(requestUserId)) {
            throw new BusinessException("해당 " + targetName + "에 대한 접근 권한이 없습니다.");
        }
    }
}
