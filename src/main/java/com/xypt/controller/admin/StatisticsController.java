package com.xypt.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.*;
import com.xypt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据统计Controller
 * 提供首页仪表盘统计数据和各类图表数据
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    @Autowired private OrdersService ordersService;
    @Autowired private UserService userService;
    @Autowired private WithdrawalService withdrawalService;
    @Autowired private EvaluationService evaluationService;
    @Autowired private BalanceRecordService balanceRecordService;

    /**
     * 首页概览统计卡片数据
     * GET /api/admin/statistics/overview
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();

        // 订单统计
        long totalOrders = ordersService.count();
        long pendingOrders = ordersService.count(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 0));
        long ongoingOrders = ordersService.count(new LambdaQueryWrapper<Orders>().in(Orders::getStatus, 1, 2, 3));
        long completedOrders = ordersService.count(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 4));
        long canceledOrders = ordersService.count(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 5));

        // 用户统计
        long totalStudents = userService.count(new LambdaQueryWrapper<User>().eq(User::getUserType, 1));
        long totalCouriers = userService.count(new LambdaQueryWrapper<User>().eq(User::getUserType, 2).eq(User::getCourierStatus, 2));
        long bannedUsers = userService.count(new LambdaQueryWrapper<User>().eq(User::getStatus, 2));

        // 今日新增数据
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now();
        long todayOrders = ordersService.count(new LambdaQueryWrapper<Orders>()
                .between(Orders::getCreateTime, todayStart, todayEnd));
        long todayUsers = userService.count(new LambdaQueryWrapper<User>()
                .between(User::getCreateTime, todayStart, todayEnd));

        // 平台总佣金（完成订单×佣金比例5%）
        List<Orders> completedList = ordersService.list(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 4));
        BigDecimal totalCommission = completedList.stream()
                .filter(o -> o.getReward() != null)
                .map(o -> o.getReward().multiply(new BigDecimal("0.05")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("totalOrders", totalOrders);
        data.put("pendingOrders", pendingOrders);
        data.put("ongoingOrders", ongoingOrders);
        data.put("completedOrders", completedOrders);
        data.put("canceledOrders", canceledOrders);
        data.put("totalStudents", totalStudents);
        data.put("totalCouriers", totalCouriers);
        data.put("bannedUsers", bannedUsers);
        data.put("todayOrders", todayOrders);
        data.put("todayUsers", todayUsers);
        data.put("totalCommission", totalCommission);

        return Result.success(data);
    }

    /**
     * 近30天订单趋势数据（折线图）
     * GET /api/admin/statistics/order-trend
     */
    @GetMapping("/order-trend")
    public Result<Map<String, Object>> orderTrend() {
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        // 获取近30天每天的订单数量
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 29; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            long count = ordersService.count(new LambdaQueryWrapper<Orders>()
                    .between(Orders::getCreateTime, start, end));
            dates.add(date.format(formatter));
            counts.add(count);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("counts", counts);
        return Result.success(data);
    }

    /**
     * 订单服务类型分布（饼图）
     * GET /api/admin/statistics/service-type-distribution
     */
    @GetMapping("/service-type-distribution")
    public Result<List<Map<String, Object>>> serviceTypeDistribution() {
        String[] types = {"代取快递", "代买", "代送"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (String type : types) {
            long count = ordersService.count(new LambdaQueryWrapper<Orders>().eq(Orders::getServiceType, type));
            Map<String, Object> item = new HashMap<>();
            item.put("name", type);
            item.put("value", count);
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 近6个月用户增长（柱状图）
     * GET /api/admin/statistics/user-growth
     */
    @GetMapping("/user-growth")
    public Result<Map<String, Object>> userGrowth() {
        List<String> months = new ArrayList<>();
        List<Long> students = new ArrayList<>();
        List<Long> couriers = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().withDayOfMonth(1).minusMonths(i);
            LocalDateTime start = monthStart.atStartOfDay();
            LocalDateTime end = monthStart.plusMonths(1).atStartOfDay();
            long studentCount = userService.count(new LambdaQueryWrapper<User>()
                    .eq(User::getUserType, 1).between(User::getCreateTime, start, end));
            long courierCount = userService.count(new LambdaQueryWrapper<User>()
                    .eq(User::getUserType, 2).between(User::getCreateTime, start, end));
            months.add(monthStart.format(formatter));
            students.add(studentCount);
            couriers.add(courierCount);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("months", months);
        data.put("students", students);
        data.put("couriers", couriers);
        return Result.success(data);
    }

    /**
     * 跑腿员业绩排行（前10名）
     * GET /api/admin/statistics/courier-ranking
     */
    @GetMapping("/courier-ranking")
    public Result<List<Map<String, Object>>> courierRanking() {
        // 统计每个跑腿员的接单量
        List<User> couriers = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, 2).eq(User::getCourierStatus, 2));

        List<Map<String, Object>> ranking = new ArrayList<>();
        for (User courier : couriers) {
            long orderCount = ordersService.count(new LambdaQueryWrapper<Orders>()
                    .eq(Orders::getCourierId, courier.getId()));
            long completedCount = ordersService.count(new LambdaQueryWrapper<Orders>()
                    .eq(Orders::getCourierId, courier.getId()).eq(Orders::getStatus, 4));
            long goodReviews = evaluationService.count(new LambdaQueryWrapper<Evaluation>()
                    .eq(Evaluation::getCourierId, courier.getId()).ge(Evaluation::getRating, 4));

            double completeRate = orderCount > 0 ? (double) completedCount / orderCount * 100 : 0;
            double goodRate = completedCount > 0 ? (double) goodReviews / completedCount * 100 : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("courierId", courier.getId());
            item.put("courierName", courier.getRealName());
            item.put("orderCount", orderCount);
            item.put("completedCount", completedCount);
            item.put("completeRate", Math.round(completeRate * 10.0) / 10.0);
            item.put("goodRate", Math.round(goodRate * 10.0) / 10.0);
            item.put("totalEarnings", courier.getTotalEarnings());
            item.put("avatar", courier.getAvatar());
            ranking.add(item);
        }

        // 按接单量降序排序，取前10
        ranking.sort((a, b) -> ((Long) b.get("orderCount")).compareTo((Long) a.get("orderCount")));
        return Result.success(ranking.size() > 10 ? ranking.subList(0, 10) : ranking);
    }

    /**
     * 近6个月收益统计（折线图）
     * GET /api/admin/statistics/revenue-trend
     */
    @GetMapping("/revenue-trend")
    public Result<Map<String, Object>> revenueTrend() {
        List<String> months = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().withDayOfMonth(1).minusMonths(i);
            LocalDateTime start = monthStart.atStartOfDay();
            LocalDateTime end = monthStart.plusMonths(1).atStartOfDay();
            // 统计当月完成订单总赏金
            List<Orders> orders = ordersService.list(new LambdaQueryWrapper<Orders>()
                    .eq(Orders::getStatus, 4).between(Orders::getCompleteTime, start, end));
            BigDecimal total = orders.stream().filter(o -> o.getReward() != null)
                    .map(Orders::getReward).reduce(BigDecimal.ZERO, BigDecimal::add);
            months.add(monthStart.format(formatter));
            revenues.add(total);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("months", months);
        data.put("revenues", revenues);
        return Result.success(data);
    }
}
