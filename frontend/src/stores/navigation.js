import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNavigationStore = defineStore('navigation', () => {
  const currentView = ref('postList')
  const selectedPostId = ref(null)
  const selectedBoardId = ref(null)

  function showPostList(boardId = null) {
    currentView.value = 'postList'
    selectedBoardId.value = boardId
    selectedPostId.value = null
  }

  function showPostDetail(postId) {
    currentView.value = 'postDetail'
    selectedPostId.value = postId
  }

  function showPostCreate() {
    currentView.value = 'postCreate'
  }

  function showBoardManage() {
    currentView.value = 'boardManage'
  }

  return { currentView, selectedPostId, selectedBoardId, showPostList, showPostDetail, showPostCreate, showBoardManage }
})
