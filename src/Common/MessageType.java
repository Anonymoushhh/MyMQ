package Common;

import java.util.HashSet;
import java.util.Set;

public final class MessageType {
	private static HashSet<Integer> set;
	public static final int ONE_WAY = 0;//������Ϣ
	public static final int REPLY_EXPECTED = 1;//��Ҫ�õ��ظ�����Ϣ
	public static final int REQUEST_QUEUE = 2;//�����,�û���������Broker�������
	public static final int REGISTER = 3;//������������Brokerע��
	public static final int PULL = 4;//������������Brokerע��
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
