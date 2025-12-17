import http from './http'

export interface LoginPayload {
  loginName: string
  password: string
}

export const login = (payload: LoginPayload) => {
  return http.post('/admin/login', payload)
}

