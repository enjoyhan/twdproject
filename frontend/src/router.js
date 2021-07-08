
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ShopManager from "./components/ShopManager"

import OrderManager from "./components/OrderManager"

import DeliveryManager from "./components/DeliveryManager"

import MessageManager from "./components/MessageManager"


import Mypage from "./components/Mypage"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/Shop',
                name: 'ShopManager',
                component: ShopManager
            },

            {
                path: '/Order',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/Delivery',
                name: 'DeliveryManager',
                component: DeliveryManager
            },

            {
                path: '/Message',
                name: 'MessageManager',
                component: MessageManager
            },


            {
                path: '/Mypage',
                name: 'Mypage',
                component: Mypage
            },


    ]
})
