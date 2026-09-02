import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect: '/leads',
      children: [
        {
          path: 'leads',
          name: 'LeadList',
          component: () => import('@/views/lead/LeadList.vue'),
          meta: { title: '线索池' },
        },
        {
          path: 'customers',
          name: 'CustomerList',
          component: () => import('@/views/customer/CustomerList.vue'),
          meta: { title: '客户池' },
        },
        {
          path: 'opportunities',
          name: 'OpportunityBoard',
          component: () => import('@/views/opportunity/OpportunityBoard.vue'),
          meta: { title: '商机管理' },
        },
        {
          path: 'schedule',
          name: 'Schedule',
          component: () => import('@/views/schedule/Schedule.vue'),
          meta: { title: '日程管理' },
        },
      ],
    },
  ],
})

export default router
