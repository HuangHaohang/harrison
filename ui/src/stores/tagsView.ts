import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'

export interface TagView extends Partial<RouteLocationNormalized> {
  title?: string
  fullPath?: string
}

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref<TagView[]>([])

  const addVisitedView = (view: RouteLocationNormalized) => {
    if (visitedViews.value.some((v) => v.path === view.path)) return
    if (view.path === '/login') return // 不添加登录页

    visitedViews.value.push(
      Object.assign({}, view, {
        title: view.meta.title || 'no-name',
        fullPath: view.fullPath
      })
    )
  }

  const delVisitedView = (view: TagView): Promise<TagView[]> => {
    return new Promise((resolve) => {
      const i = visitedViews.value.findIndex((v) => v.path === view.path)
      if (i > -1) {
        visitedViews.value.splice(i, 1)
      }
      resolve([...visitedViews.value])
    })
  }

  return {
    visitedViews,
    addVisitedView,
    delVisitedView
  }
})
