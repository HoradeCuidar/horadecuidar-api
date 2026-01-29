package com.hdc.hdc.service.interfaces;

import com.hdc.hdc.dto.auth.AuthDTO;
import com.hdc.hdc.dto.response.AuthResponseDTO;

public interface IAuthService {

    AuthResponseDTO logar(AuthDTO usuario);
}