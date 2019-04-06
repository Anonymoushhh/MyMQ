package Common;

import java.util.HashSet;
import java.util.Set;

public final class MessageType {
	static HashSet<Integer> set;
	public static final int ONE_WAY = 0;//单向消息
	public static final int REPLY_EXPECTED = 1;//需要得到回复的消息
	public static final int HEART_BEAT = 2;//心跳包
	static {
		set = new HashSet<Integer>();
		set.add(ONE_WAY);
		set.add(REPLY_EXPECTED);
		set.add(HEART_BEAT);
	}
	public static Set getSet() {
		return set;
	}

}
