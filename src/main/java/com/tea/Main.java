package com.tea;

import com.tea.entity.*;
import com.tea.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;

public class Main {
    private static final Scanner SC = new Scanner(System.in);
    private static final AdminService adminSvc = new AdminService();
    private static final DrinkService drinkSvc = new DrinkService();
    private static final MemberService memberSvc = new MemberService();
    private static final OrderService orderSvc = new OrderService();
    private static final StatService statSvc = new StatService();
    private static Admin currentAdmin;

    public static void main(String[] args) {
        printWelcome();
        while(true){
            if(currentAdmin==null){
            if(!doLoginMenu()){
                System.out.println("再见!");
                return;
            }
            }else{
                showMainMenu();
            }
        }
    }

    private static void printWelcome() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       🧋  奶茶物语 · 点单系统  v1.0           ║");
        System.out.println("║         欢迎使用，请先登录                     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
    private static boolean doLoginMenu() {
        System.out.println();
        System.out.println("  1. 登录");
        System.out.println("  2. 注册新账号");
        System.out.println("  0. 退出系统");
        System.out.print("请选择 > ");
        switch (readLine()) {
            case "1" -> doLogin();
            case "2" -> doRegister();
            case "0" -> { return false; }
            default  -> System.out.println("✗ 无效选项");
        }
        return true;
    }
    private static void doLogin() {
        System.out.print("请输入用户名 > "); String u = readLine();
        System.out.print("请输入密码   > "); String p = readLine();
        Admin a = adminSvc.login(u, p);
        if (a == null) {
            System.out.println("✗ 用户名或密码错误，请重试。");
            return;
        }
        currentAdmin = a;
        System.out.println("✓ 登录成功，欢迎回来，" + a.getUsername() + "！");
    }
    private static void doRegister() {
        System.out.print("用户名 > "); String u = readLine();
        System.out.print("密码（≥6位）> "); String p = readLine();
        try {
            if (adminSvc.register(u, p)) {
                System.out.println("✓ 注册成功，请登录。");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("✗ " + e.getMessage());
        }
    }
    private static void showMainMenu() {
        System.out.println();
        System.out.println("╔════════════ 主菜单 [" + currentAdmin.getUsername() + "] ════════════╗");
        System.out.println("║   1. 商品管理                                ║");
        System.out.println("║   2. 会员管理                                ║");
        System.out.println("║   3. 下单结算  ← 最常用                      ║");
        System.out.println("║   4. 数据统计                                ║");
        System.out.println("║   0. 退出登录                                ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.print("请选择 > ");
        switch (readLine()) {
            case "1" -> drinkMenu();
            case "2" -> memberMenu();
            case "3" -> orderMenu();
            case "4" -> statMenu();
            case "0" -> { currentAdmin = null; System.out.println("✓ 已退出登录"); }
            default  -> System.out.println("✗ 无效选项");
        }
    }
    private static void drinkMenu() {
        while (true) {
            System.out.println();
            System.out.println("┌────────── 商品管理 ──────────┐");
            System.out.println("│  1. 查看全部奶茶             │");
            System.out.println("│  2. 新增奶茶                 │");
            System.out.println("│  3. 修改奶茶                 │");
            System.out.println("│  4. 下架奶茶                 │");
            System.out.println("│  5. 库存预警查询             │");
            System.out.println("│  0. 返回主菜单               │");
            System.out.println("└──────────────────────────────┘");
            System.out.print("请选择 > ");
            String op = readLine();
            if ("0".equals(op)) return;
            switch (op) {
                case "1" -> listDrinks();
                case "2" -> addDrink();
                case "3" -> updateDrink();
                case "4" -> deactivateDrink();
                case "5" -> lowStock();
                default  -> System.out.println("✗ 无效选项");
            }
        }
    }
    private static void listDrinks() {
        List<Drink> list = drinkSvc.listAllActive();
        System.out.println();
        System.out.println("========= 在售奶茶清单 =========");
        String currentCat = null;
        for (Drink d : list) {
            if (!d.getCategory().equals(currentCat)) {
                currentCat = d.getCategory();
                System.out.println("【" + currentCat + "】");
            }
            String warn = d.getStock() < 10 ? "  ⚠ 库存预警" : "";
            System.out.printf("  [%d] %-10s ¥%-6s 库存 %2d%s%n",
                    d.getId(), d.getName(), d.getPrice().toPlainString(),
                    d.getStock(), warn);
        }
        System.out.println("================================");
    }

    private static void addDrink() {
        System.out.print("名称        > "); String n = readLine();
        System.out.print("分类        > "); String c = readLine();
        System.out.print("单价（元）  > "); BigDecimal p = new BigDecimal(readLine());
        System.out.print("初始库存    > "); int s = Integer.parseInt(readLine());
        try {
            if (drinkSvc.addDrink(n, c, p, s)) {
                System.out.println("✓ 已新增「" + n + "」");
            }
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
    }

    private static void updateDrink() {
        System.out.print("要修改的奶茶 ID > "); Long id = Long.parseLong(readLine());
        Drink d = drinkSvc.findById(id);
        if (d == null) { System.out.println("✗ 奶茶不存在"); return; }
        System.out.println("当前：" + d);
        System.out.print("新单价       > "); BigDecimal p = new BigDecimal(readLine());
        System.out.print("新库存       > "); int s = Integer.parseInt(readLine());
        if (drinkSvc.updateDrink(id, p, s)) System.out.println("✓ 已更新");
        else System.out.println("✗ 更新失败");
    }

    private static void deactivateDrink() {
        System.out.print("要下架的奶茶 ID > "); Long id = Long.parseLong(readLine());
        if (drinkSvc.deactivate(id)) System.out.println("✓ 已下架（历史订单仍可查到）");
        else System.out.println("✗ 下架失败");
    }

    private static void lowStock() {
        List<Drink> list = drinkSvc.listLowStock();
        System.out.println();
        System.out.println("========= 库存预警（< 10 杯）=========");
        if (list.isEmpty()) {
            System.out.println("  ✓ 暂无库存预警，安心营业");
        } else {
            for (Drink d : list) {
                System.out.printf("  [%d] %-10s 库存 %d%n", d.getId(), d.getName(), d.getStock());
            }
        }
        System.out.println("======================================");
    }
    private static void memberMenu() {
        while (true) {
            System.out.println();
            System.out.println("┌────────── 会员管理 ──────────┐");
            System.out.println("│  1. 按手机号查会员           │");
            System.out.println("│  2. 新增会员                 │");
            System.out.println("│  3. 查看全部会员             │");
            System.out.println("│  0. 返回主菜单               │");
            System.out.println("└──────────────────────────────┘");
            System.out.print("请选择 > ");
            String op = readLine();
            if ("0".equals(op)) return;
            switch (op) {
                case "1" -> findMember();
                case "2" -> addMember();
                case "3" -> listMembers();
                default  -> System.out.println("✗ 无效选项");
            }
        }
    }

    private static void findMember() {
        System.out.print("手机号 > "); String phone = readLine();
        Member m = memberSvc.findByPhone(phone);
        if (m == null) System.out.println("✗ 未找到该会员");
        else System.out.println(m);
    }

    private static void addMember() {
        System.out.print("姓名 > "); String n = readLine();
        System.out.print("手机号 > "); String p = readLine();
        try {
            if (memberSvc.addMember(n, p)) System.out.println("✓ 新会员入会成功");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
    }

    private static void listMembers() {
        List<Member> list = memberSvc.listAll();
        list.forEach(System.out::println);
    }
    private static void orderMenu() {
        while (true) {
            System.out.println();
            System.out.println("┌────────── 下单结算 ──────────┐");
            System.out.println("│  1. 新建订单                 │");
            System.out.println("│  2. 查询订单明细             │");
            System.out.println("│  0. 返回主菜单               │");
            System.out.println("└──────────────────────────────┘");
            System.out.print("请选择 > ");
            String op = readLine();
            if ("0".equals(op)) return;
            switch (op) {
                case "1" -> placeOrder();
                case "2" -> queryOrder();
                default  -> System.out.println("✗ 无效选项");
            }
        }
    }

    private static void placeOrder() {
        // Step A：识别会员 / 散客
        System.out.print("请输入会员手机号（输入 0 表示散客）> ");
        String phone = readLine();
        Member member = null;
        if (!"0".equals(phone)) {
            member = memberSvc.findByPhone(phone);
            if (member == null) {
                System.out.println("✗ 该手机号未注册，按散客处理（如需注册请到「会员管理 -> 新增会员」）");
            } else {
                System.out.println("✓ 识别到会员：" + member.getName() + "（积分 " + member.getPoints() + "）");
            }
        }
        listDrinks();
        List<OrderItem> items = new ArrayList<>();
        BigDecimal preview = BigDecimal.ZERO;
        while (true) {
            System.out.print("请输入奶茶ID（输入 0 表示完成下单）> ");
            String idStr = readLine();
            if ("0".equals(idStr)) break;
            Long drinkId;
            try { drinkId = Long.parseLong(idStr); }
            catch (NumberFormatException e) { System.out.println("✗ 请输入数字"); continue; }

            Drink d = drinkSvc.findById(drinkId);
            if (d == null || d.getIsActive() == 0) {
                System.out.println("✗ 奶茶不存在或已下架"); continue;
            }
            System.out.print("杯数 > ");
            int qty;
            try { qty = Integer.parseInt(readLine()); }
            catch (NumberFormatException e) { System.out.println("✗ 请输入数字"); continue; }
            if (qty <= 0) { System.out.println("✗ 杯数必须 ≥ 1"); continue; }
            if (qty > d.getStock()) {
                System.out.printf("✗ 库存不足！%s 仅剩 %d 杯%n", d.getName(), d.getStock()); continue;
            }

            OrderItem it = new OrderItem(d.getId(), qty, d.getPrice());
            it.setDrinkName(d.getName());
            items.add(it);
            preview = preview.add(it.getSubtotal());
            System.out.printf("✓ 已加入：%s × %d = ¥%s%n",
                    d.getName(), qty, it.getSubtotal().toPlainString());
        }

        if (items.isEmpty()) {
            System.out.println("订单已取消（未选任何奶茶）");
            return;
        }
        System.out.println();
        System.out.println("========= 订单清单确认 =========");
        System.out.println("  会员：" + (member == null ? "散客" : member.getName() + "（" + member.getPhone() + "）"));
        System.out.println("  ─────────────────────────────");
        for (OrderItem it : items) {
            System.out.printf("  %-10s × %-2d  = ¥%s%n",
                    it.getDrinkName(), it.getQuantity(), it.getSubtotal().toPlainString());
        }
        System.out.println("  ─────────────────────────────");
        System.out.printf("  合计           ¥%s%n", preview.toPlainString());
        if (member != null) {
            System.out.printf("  预计积分 +%d%n", preview.intValue());
        }
        System.out.println("================================");
        System.out.print("确认下单？(y/n) > ");
        if (!"y".equalsIgnoreCase(readLine())) {
            System.out.println("订单已取消"); return;
        }
        try {
            OrderService.PlaceOrderResult result = orderSvc.placeOrder(
                    member == null ? null : member.getId(), items);
            System.out.println();
            System.out.println("✓ 订单创建成功！");
            System.out.println("  订单号 : " + result.OrderId());
            System.out.println("  总金额 : ¥" + result.totalAmount().toPlainString());
            if (result.pointsAfter() != null) {
                System.out.printf("  会员「%s」积分 %d → %d%n",
                        member.getName(), member.getPoints(), result.pointsAfter());
            }
            System.out.println("  库存已扣减，谢谢惠顾 🧋");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
    }

    private static void queryOrder() {
        System.out.print("请输入订单号 > ");
        Long id;
        try { id = Long.parseLong(readLine()); }
        catch (NumberFormatException e) { System.out.println("✗ 请输入数字"); return; }

        TeaOrder o = orderSvc.findOrderById(id);
        if (o == null) { System.out.println("✗ 订单不存在"); return; }
        List<OrderItem> items = orderSvc.findItemsByOrderId(id);

        Member m = (o.getMemberId() == null) ? null : memberSvc.findById(o.getMemberId());

        System.out.println();
        System.out.printf("========= 订单 #%d 详情 =========%n", o.getId());
        System.out.println("  会员    : " + (m == null ? "散客" : m.getName() + "（" + m.getPhone() + "）"));
        System.out.println("  下单时间: " + o.getCreatedAt());
        System.out.println("  状态    : " + ("PAID".equals(o.getStatus()) ? "已支付" : "已取消"));
        System.out.println("  ──────────────────────────────");
        for (OrderItem it : items) {
            System.out.printf("  %-10s × %-2d  ¥%s = ¥%s%n",
                    it.getDrinkName(), it.getQuantity(),
                    it.getUnitPrice().toPlainString(), it.getSubtotal().toPlainString());
        }
        System.out.println("  ──────────────────────────────");
        System.out.printf("  合计                       ¥%s%n", o.getTotalAmount().toPlainString());
        System.out.println("================================");
    }

    private static void statMenu() {
        while (true) {
            System.out.println();
            System.out.println("┌────────── 数据统计 ──────────┐");
            System.out.println("│  1. 本月销量榜 TOP 3         │");
            System.out.println("│  2. 本月营业额               │");
            System.out.println("│  3. 库存预警清单             │");
            System.out.println("│  0. 返回主菜单               │");
            System.out.println("└──────────────────────────────┘");
            System.out.print("请选择 > ");
            String op = readLine();
            if ("0".equals(op)) return;
            switch (op) {
                case "1" -> topSales();
                case "2" -> monthlyAmount();
                case "3" -> lowStock();
                default  -> System.out.println("✗ 无效选项");
            }
        }
    }

    private static void topSales() {
        var list = statSvc.topSalesThisMonth(3);
        System.out.println();
        System.out.println("=========  本月销量榜 TOP 3  =========");
        String[] medals = {"🥇", "🥈", "🥉"};
        if (list.isEmpty()) System.out.println("  暂无销售数据");
        for (int i = 0; i < list.size(); i++) {
            var r = list.get(i);
            System.out.printf("  %s %-10s 售出 %d 杯  ¥%s%n",
                    medals[i], r.drinkName(), r.totalQty(), r.totalAmount().toPlainString());
        }
        System.out.println("======================================");
    }

    private static void monthlyAmount() {
        var s = statSvc.monthlySummary();
        System.out.println();
        System.out.println("============= 本月营业额 =============");
        System.out.println("  统计周期 : " + s.period());
        System.out.println("  订单总数 : " + s.orderCount());
        System.out.println("  营业总额 : ¥" + s.totalAmount().toPlainString());
        System.out.println("  客单价   : ¥" + s.avgAmount().setScale(2, RoundingMode.HALF_UP).toPlainString());
        System.out.println("======================================");
    }

    // ============ 工具 ============

    private static String readLine() {
        return SC.nextLine().trim();
    }
}
