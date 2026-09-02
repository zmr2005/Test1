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
          name: 'CustomerPool',
          component: () => import('@/views/customer/CustomerPool.vue'),
          meta: { title: '客户池' },
        },
        {
          path: 'customers/:id',
          name: 'CustomerDetail',
          component: () => import('@/views/customer/CustomerDetail.vue'),
          meta: { title: '客户档案' },
        },
        {
          path: 'opportunities',
          name: 'Opportunity',
          component: () => import('@/views/opportunity/Opportunity.vue'),
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
