import request from './request'

export function getComments(postId) {
  return request.get(`/posts/${postId}/comments`)
}

export function createComment(postId, data) {
  return request.post(`/posts/${postId}/comments`, data)
}
