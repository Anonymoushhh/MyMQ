package Test;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.concurrent.ScheduledThreadPoolExecutor;  
import java.util.concurrent.TimeUnit;  
  
public class TestScheduledThreadPoolExecutor {  
    private static SimpleDateFormat format = new SimpleDateFormat(  
            "yyyy-MM-dd HH:mm:ss");  
  
    public static void main(String[] args) {  
        // ScheduledExecutorService exec=Executors.newScheduledThreadPool(1);  
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);  
        /** 
         * 每隔一段时间打印系统时间，互不影响的<br/> 创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；<br/> 
         * 也就是将在 initialDelay 后开始执行，然后在initialDelay+period 后执行，<br/> 接着在 
         * initialDelay + 2 * period 后执行，依此类推。 
         */  
//        exec.scheduleAtFixedRate(new Runnable() {  
//            public void run() {  
//                System.out.println(format.format(new Date()));  
//            }  
//        }, 1000, 5000, TimeUnit.MILLISECONDS);  
  
        // 开始执行后就触发异常,next周期将不会运行  
//        exec.scheduleAtFixedRate(new Runnable() {  
//            public void run() {  
//                System.out  
//                        .println("RuntimeException no catch,next time can't run");  
//                throw new RuntimeException();  
//            }  
//        }, 1000, 5000, TimeUnit.MILLISECONDS);  
  
        // 虽然抛出了运行异常,当被拦截了,next周期继续运行  
//        exec.scheduleAtFixedRate(new Runnable() {  
//            public void run() {  
//                try {  
//                    throw new RuntimeException();  
//                } catch (Exception e) {  
//                    System.out.println("RuntimeException catched,can run next");  
//                }  
//            }  
//        }, 1000, 5000, TimeUnit.MILLISECONDS);  
  
        /** 
         * 创建并执行一个在给定初始延迟后首次启用的定期操作，<br/> 随后，在每一次执行终止和下一次执行开始之间都存在给定的延迟。 
         */  
//        exec.scheduleWithFixedDelay(new Runnable() {  
//            public void run() {  
//                System.out.println("scheduleWithFixedDelay:begin,"  
//                        + format.format(new Date()));  
//                try {  
//                    Thread.sleep(2000);  
//                } catch (InterruptedException e) {  
//                    e.printStackTrace();  
//                }  
//                System.out.println("scheduleWithFixedDelay:end,"  
//                        + format.format(new Date()));  
//            }  
//        }, 1000, 5000, TimeUnit.MILLISECONDS);  
  
        /** 
         * 创建并执行在给定延迟后启用的一次性操作。 
         */  
        exec.schedule(new Runnable() {  
            public void run() {  
                System.out.println("The thread can only run once!");  
            }  
        }, 5000, TimeUnit.MILLISECONDS);  
    }  
}  