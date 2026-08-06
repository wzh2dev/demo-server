package com.demo.server;

import com.demo.server.order.repository.OrderRepository;
import com.demo.server.order.service.OrderService;
import com.demo.server.order.web.OrderController;
import com.demo.server.product.repository.ProductRepository;
import com.demo.server.product.service.ProductService;
import com.demo.server.product.web.ProductController;
import com.demo.server.user.repository.UserRepository;
import com.demo.server.user.service.UserService;
import com.demo.server.user.web.UserController;

/**
 * 应用启动入口，仅用于演示依赖装配。
 */
public class Application {

    public static void main(String[] args) {
        // 装配 user
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);

        // 装配 product
        ProductRepository productRepository = new ProductRepository();
        ProductService productService = new ProductService(productRepository);
        ProductController productController = new ProductController(productService);

        // 装配 order
        OrderRepository orderRepository = new OrderRepository();
        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        System.out.println("Application initialized.");
        System.out.println("UserController = " + userController.getClass().getName());
        System.out.println("ProductController = " + productController.getClass().getName());
        System.out.println("OrderController = " + orderController.getClass().getName());
    }
}
