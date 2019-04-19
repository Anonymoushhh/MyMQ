package Common;

import java.util.HashSet;
import java.util.Set;

public final class MessageType {
	private static HashSet<Integer> set;
	public static final int ONE_WAY = 0;//单向消息
	public static final int REPLY_EXPECTED = 1;//需要得到回复的消息
	public static final int REQUEST_QUEUE = 2;//请求包,用户生产者向Broker申请队列
	public static final int REGISTER = 3;//用于消费者向Broker注册
	public static final int PULL = 4;//用于消费者向Broker注册
	static {
		set = new HashSet<Integer>();
		set.add(ONE_WAY);
		set.add(REPLY_EXPECTED);
		set.add(REQUEST_QUEUE);
		set.add(REGISTER);
		set.add(PULL);
	}
	private static Set<Integer> getSet() {
		return set;
	}
	public static boolean contains(Integer i) {
		return MessageType.getSet().contains(i);
	}
//	public static void main(String[] args) {
//		System.out.println(MessageType.contains(2));
//	}
}
