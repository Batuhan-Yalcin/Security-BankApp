export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber: string;
  address: string;
  roles?: string[];
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface JwtResponse {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  username?: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface TokenRefreshResponse {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  status: number;
  error?: string;
} 