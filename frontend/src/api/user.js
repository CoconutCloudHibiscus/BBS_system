import request from './request'

export function getUserInfo() {
  return request.get('/user/me')
}

export function updateNickname(data) {
  return request.put('/user/nickname', data)
}

export function deleteAccount() {
  return request.delete('/user/me')
}
