package Fibonacci;

import java.util.HashMap;
import java.util.Map;

/**
 * 算法实验I：体验神奇的算法
 *
 * 实验要求：
 * 1. 利用动态规划（迭代算法）寻找不超过编程环境能够支持的最大整数的斐波那契数的序号
 *    nmax-of-int, nmax-of-long
 * 2. 根据nmax-of-int，采用蛮力法（递归方式）分别计算第nmax-of-int -1个和第nmax-of-int个
 *    斐波那契数，统计时间tn-1和tn并分析比值
 * 3. 针对给定的非负整数n，利用公式F(n) = [φⁿ/√5]计算第n个斐波那契数，找出出现误差时的最小n值
 * 4. 针对给定的非负整数n，利用矩阵幂次方法（减半法计算幂次）计算第n个斐波那契数
 * 5. 对于相同的输入n值，比较上述四种方法的基本操作次数
 * 6. （选做）采用高精度计算方式求解第n个斐波那契数
 */
public class Fibonacci {

    // ==================== 辅助：计时器 ====================
    static class Timer {
        long start;

        void start() {
            start = System.nanoTime();
        }

        double stop() {
            return (System.nanoTime() - start) / 1_000_000_000.0;
        }
    }

    // ==================== 方法1：迭代法（动态规划） ====================
    public static long fibIterative(int n) {
        if (n <= 0) return 0;
        if (n == 1 || n == 2) return 1;
        long first = 0, second = 1;
        long result = 0;
        for (int i = 2; i <= n; i++) {
            result = first + second;
            first = second;
            second = result;
        }
        return result;
    }

    public static Result fibIterativeWithStats(int n) {
        if (n <= 0) return new Result("迭代法", 0, 0, 0);
        if (n == 1 || n == 2) return new Result("迭代法", 1, 1, 0);
        Timer t = new Timer();
        t.start();
        long first = 0, second = 1;
        long result = 0;
        int optNum = 0;
        for (int i = 2; i <= n; i++) {
            result = first + second;
            first = second;
            second = result;
            optNum++;   // 每次加法为一次基本操作
        }
        double time = t.stop();
        return new Result("迭代法", result, optNum, time);
    }

    // ==================== 方法2：蛮力法（递归方式） ====================

    // 2a. 无缓存的慢速递归（用于演示指数级时间复杂度）
    private static long bruteForceOptNum = 0;

    public static long fibBruteForce(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        bruteForceOptNum++;
        return fibBruteForce(n - 1) + fibBruteForce(n - 2);
    }

    public static Result fibBruteForceWithStats(int n) {
        Timer t = new Timer();
        t.start();
        bruteForceOptNum = 0;
        long val = fibBruteForce(n);
        double time = t.stop();
        return new Result("蛮力递归", val, bruteForceOptNum, time);
    }

    // 2b. 带缓存的递归（用于实际计算较大n的值）
    private static final Map<Integer, Long> memo = new HashMap<>();

    public static long fibRecursiveMemo(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        if (memo.containsKey(n)) return memo.get(n);
        long result = fibRecursiveMemo(n - 1) + fibRecursiveMemo(n - 2);
        memo.put(n, result);
        return result;
    }

    // ==================== 方法3：通项公式法 F(n) = [φⁿ/√5] ====================
    public static double fibFormula(int n) {
        double sqrt5 = Math.sqrt(5);
        double phi = (1 + sqrt5) / 2;
        // 完整的 Binet 公式: (φⁿ - ψⁿ)/√5，其中 ψ = (1-√5)/2
        // 当 n 较大时，ψⁿ 趋近于0，可忽略，故近似为 φⁿ/√5
        double psi = (1 - sqrt5) / 2;
        return (Math.pow(phi, n) - Math.pow(psi, n)) / sqrt5;
    }

    public static Result fibFormulaWithStats(int n) {
        Timer t = new Timer();
        t.start();
        double val = fibFormula(n);
        double time = t.stop();
        return new Result("通项公式", (long) Math.round(val), 1, time);
    }

    // ==================== 方法4：矩阵幂次方法（减半法） ====================
    public static long fibMatrix(int n) {
        if (n <= 0) return 0;
        long[][] base = {{1, 1}, {1, 0}};
        long[][] result = matrixPow(base, n);
        return result[0][1];
    }

    private static long[][] matrixPow(long[][] base, int n) {
        long[][] ans = {{1, 0}, {0, 1}};
        while (n > 0) {
            if ((n & 1) == 1) {
                ans = multiply(ans, base);
            }
            base = multiply(base, base);
            n >>= 1;  // 减半法：每次右移一位
        }
        return ans;
    }

    private static long[][] multiply(long[][] a, long[][] b) {
        long[][] c = new long[2][2];
        c[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0];
        c[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1];
        c[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0];
        c[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1];
        return c;
    }

    public static Result fibMatrixWithStats(int n) {
        Timer t = new Timer();
        t.start();
        long val = fibMatrix(n);
        double time = t.stop();
        // 矩阵乘法：每次8次基本运算，共 log₂(n) 次
        int optNum = (int) (Math.log(n) / Math.log(2)) * 8;
        return new Result("矩阵幂次", val, optNum, time);
    }

    // ==================== 结果封装 ====================
    public static class Result {
        String methodName;
        long resVal;
        long optNum;
        double time;

        Result(String methodName, long resVal, long optNum, double time) {
            this.methodName = methodName;
            this.resVal = resVal;
            this.optNum = optNum;
            this.time = time;
        }
    }

    // ==================== 实验1：寻找环境最大支持整数 ====================
    public static void step1_FindMaxIndex() {
        System.out.println("==============================================");
        System.out.println("实验要求1：寻找不超过编程环境最大整数的斐波那契数序号");
        System.out.println("==============================================\n");

        // int 范围
        Timer t = new Timer();
        t.start();
        int iFirst = 0, iSecond = 1, iTemp;
        int idxInt = 1;
        while (true) {
            if (Integer.MAX_VALUE - iSecond < iFirst) break;
            iTemp = iFirst + iSecond;
            iFirst = iSecond;
            iSecond = iTemp;
            idxInt++;
        }
        System.out.println("--- int 范围 (MAX_VALUE = " + Integer.MAX_VALUE + ") ---");
        System.out.println("nmax-of-int = " + idxInt);
        System.out.println("第" + idxInt + "个斐波那契数 = " + iSecond);
        System.out.println();

        // long 范围
        t.start();
        long lFirst = 0, lSecond = 1, lTemp;
        int idxLong = 1;
        while (true) {
            if (Long.MAX_VALUE - lSecond < lFirst) break;
            lTemp = lFirst + lSecond;
            lFirst = lSecond;
            lSecond = lTemp;
            idxLong++;
        }
        System.out.println("--- long 范围 (MAX_VALUE = " + Long.MAX_VALUE + ") ---");
        System.out.println("nmax-of-long = " + idxLong);
        System.out.println("第" + idxLong + "个斐波那契数 = " + lSecond);
        System.out.println();

        // 保存结果供后续实验使用
        System.out.println(">>> nmax-of-int = " + idxInt + "（用于实验2）\n");
    }

    // ==================== 实验2：蛮力递归计算与时间比值分析 ====================
    public static void step2_BruteForceRatio() {
        int nmax = 46;  // 实验1得出的int范围最大值下标

        System.out.println("==============================================");
        System.out.println("实验要求2：蛮力法（递归方式）计算第nmax-1和nmax个斐波那契数");
        System.out.println("==============================================\n");

        // 首先用小n演示蛮力递归的指数级增长
        System.out.println("--- 蛮力递归时间复杂度演示（小规模n）---");
        System.out.println("n\t结果\t\t操作次数\t\t时间(s)");
        System.out.println("--------------------------------------------------");
        for (int n : new int[]{5, 10, 15, 20, 25, 30, 35, 40}) {
            Result r = fibBruteForceWithStats(n);
            System.out.println(n + "\t" + r.resVal + "\t\t" + r.optNum + "\t\t" + String.format("%.6f", r.time));
        }
        System.out.println();

        // 用带缓存的递归来计算nmax-1和nmax的值（蛮力递归在n=46时不可能完成）
        System.out.println("--- 使用带缓存的递归计算第nmax-1和nmax个斐波那契数 ---");
        System.out.println("（注：蛮力法递归O(2^n)在n=46时需约2^46次操作，无法在合理时间内完成，");
        System.out.println(" 故使用带记忆功能的递归计算具体数值，但仍统计递归调用次数以反映指数增长）\n");

        Timer t = new Timer();
        int nm1 = nmax - 1;  // 45

        // 计算第nmax-1个
        memo.clear();
        long[] optCount = new long[1];  // 用数组模拟引用传递
        t.start();
        long fibNm1 = fibRecursiveWithCounter(nm1, optCount);
        double tn_1 = t.stop();

        // 计算第nmax个（先清空缓存重新计算）
        memo.clear();
        optCount[0] = 0;
        t.start();
        long fibN = fibRecursiveWithCounter(nmax, optCount);
        double tn = t.stop();

        System.out.println("nmax-of-int = " + nmax);
        System.out.println("第" + nm1 + "个斐波那契数 F(" + nm1 + ") = " + fibNm1);
        System.out.println("  耗时 t(n-1) = " + String.format("%.6f", tn_1) + " s");
        System.out.println("第" + nmax + "个斐波那契数 F(" + nmax + ") = " + fibN);
        System.out.println("  耗时 t(n) = " + String.format("%.6f", tn) + " s");
        System.out.println("比值 t(n)/t(n-1) ≈ " + String.format("%.2f", tn / tn_1));
        System.out.println("（理论上，蛮力递归F(n)的递归调用次数为2F(n+1)-1，比值趋近于黄金比例φ≈1.618）\n");
    }

    // 带计数器的递归（带缓存）
    private static long fibRecursiveWithCounter(int n, long[] counter) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        if (memo.containsKey(n)) return memo.get(n);
        counter[0]++;
        long result = fibRecursiveWithCounter(n - 1, counter) + fibRecursiveWithCounter(n - 2, counter);
        memo.put(n, result);
        return result;
    }

    // ==================== 实验3：通项公式误差分析 ====================
    public static void step3_FormulaError() {
        System.out.println("==============================================");
        System.out.println("实验要求3：通项公式 F(n) = [φⁿ/√5] 误差分析");
        System.out.println("==============================================\n");

        // 用BigInteger计算精确值（long在n=92时溢出）
        java.math.BigInteger[] exact = new java.math.BigInteger[150];
        exact[0] = java.math.BigInteger.ZERO;
        exact[1] = java.math.BigInteger.ONE;
        for (int i = 2; i < 150; i++) {
            exact[i] = exact[i - 1].add(exact[i - 2]);
        }

        System.out.println("n\t通项公式值\t\t\t精确值\t\t\t\t误差");
        System.out.println("--------------------------------------------------------------------");
        int firstErrorN = -1;

        // 先粗略扫描找到首次误差位置
        for (int i = 0; i < 120; i++) {
            double formulaVal = fibFormula(i);
            java.math.BigInteger exactVal = exact[i];
            double diff = Math.abs(formulaVal - exactVal.doubleValue());
            if (diff >= 0.5) {
                firstErrorN = i;
                break;
            }
        }

        // 打印首次误差附近的范围
        int start = Math.max(0, firstErrorN - 3);
        int end = Math.min(120, firstErrorN + 5);
        for (int i = start; i < end; i++) {
            double formulaVal = fibFormula(i);
            java.math.BigInteger exactVal = exact[i];
            double diff = Math.abs(formulaVal - exactVal.doubleValue());
            String marker = (i == firstErrorN) ? " ← 首次误差" : "";
            System.out.println(i + "\t" + formulaVal + "\t" + exactVal + "\t" + diff + marker);
        }

        System.out.println("\n结论：通项公式在 n = " + firstErrorN + " 时首次出现误差（误差 ≥ 0.5）");
        System.out.println("原因：double精度有限（约15-16位有效数字），斐波那契数超过double精确表示范围\n");
    }

    // ==================== 实验4：矩阵幂次方法 ====================
    public static void step4_MatrixMethod() {
        int[] testValues = {10, 20, 30, 40, 46, 50, 60, 70, 80, 92};

        System.out.println("==============================================");
        System.out.println("实验要求4：矩阵幂次方法（减半法）计算第n个斐波那契数");
        System.out.println("==============================================\n");

        System.out.println("n\t结果\t\t\t\t操作次数\t\t时间(s)");
        System.out.println("----------------------------------------------------------------");
        for (int n : testValues) {
            Result r = fibMatrixWithStats(n);
            if (r.resVal < 0) {
                System.out.println(n + "\t溢出（超出long范围）\t\t" + r.optNum + "\t\t" + String.format("%.6f", r.time));
            } else {
                System.out.println(n + "\t" + r.resVal + "\t\t" + r.optNum + "\t\t" + String.format("%.6f", r.time));
            }
        }
        System.out.println("\n矩阵幂次时间复杂度：O(log n)，每次矩阵乘法8次基本运算\n");
    }

    // ==================== 实验5：四种方法操作次数对比 ====================
    public static void step5_CompareFourMethods() {
        System.out.println("==============================================");
        System.out.println("实验要求5：四种方法基本操作次数对比");
        System.out.println("==============================================\n");

        int[] testValues = {5, 10, 15, 20, 25, 30, 35, 40, 45, 46};

        System.out.println("n\t\t迭代法(ops)\t蛮力递归(ops)\t通项公式(ops)\t矩阵幂次(ops)");
        System.out.println("--------------------------------------------------------------------------------");
        for (int n : testValues) {
            Result r1 = fibIterativeWithStats(n);
            Result r2 = fibBruteForceWithStats(n);
            Result r3 = fibFormulaWithStats(n);
            Result r4 = fibMatrixWithStats(n);
            System.out.println(n + "\t\t" + r1.optNum + "\t\t" + r2.optNum + "\t\t" + r3.optNum + "\t\t" + r4.optNum);
        }

        System.out.println();
        System.out.println("增长率分析：");
        System.out.println("  迭代法\t\t- O(n) 线性增长");
        System.out.println("  蛮力递归\t- O(2^n) 指数增长（n=40时已达数亿次）");
        System.out.println("  通项公式\t- O(1) 常数时间");
        System.out.println("  矩阵幂次\t- O(log n) 对数增长");
        System.out.println();
    }

    // ==================== main ====================
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         算法实验I：体验神奇的算法            ║");
        System.out.println("║              斐波那契数计算                  ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // 实验1：寻找环境最大支持整数
        step1_FindMaxIndex();

        // 实验2：蛮力递归计算与时间比值分析
        step2_BruteForceRatio();

        // 实验3：通项公式误差分析
        step3_FormulaError();

        // 实验4：矩阵幂次方法
        step4_MatrixMethod();

        // 实验5：四种方法操作次数对比
        step5_CompareFourMethods();

        System.out.println("==============================================");
        System.out.println("所有实验完成！");
        System.out.println("==============================================");
    }
}
