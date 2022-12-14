package com.mtld.backend.service.user;

import com.mtld.backend.dto.token.KakaoTokenDto;
import com.mtld.backend.dto.token.ReissueDto;
import com.mtld.backend.dto.token.TokenDto;
import com.mtld.backend.dto.user.LoginResponseDto;
import com.mtld.backend.dto.user.UserInfoDto;
import com.mtld.backend.entity.user.User;

/**
 * created by seongmin on 2022/09/15
 * updated by seongmin on 2022/09/23
 */
public interface UserService {

    UserInfoDto getUserById(Long id);

    KakaoTokenDto getKakaoAccessToken(String code);

    User getKakaoInfo(String kakaoAccessToken);

    LoginResponseDto kakaoLogin(String kakaoAccessToken);

    void logout(Long id);

    TokenDto reissue(ReissueDto reissueDto);

    UserInfoDto getMyInfoSecret();


}
