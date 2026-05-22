import request from './request'

export function getPosts(params) {
  return request.get('/posts', { params })
}

export function getPostDetail(id) {
  return request.get(`/posts/${id}`)
}

export function createPost(data) {
  return request.post('/posts', data)
}

export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}
