package Utils;

import java.util.Set;

import Common.Message;
import Common.MessageType;

public class MessageUtil {
	public static String MessageConvertToString(Message msg) {
		String str_len = String.format("%04d",msg.getLength());
		String str_type = String.valueOf(msg.getType());
		String JointMessage = str_len+str_type+msg.getMessage();
		return JointMessage;
	}
	public static Message StringConvertToMessage(String str) {
		str = str.trim();
		if(str.length()<5)
			return null;
		String str_len = str.substring(0, 3);
		Integer length = Integer.valueOf(str_len);
		//type
		char str_type = str.charAt(4);
		//¼ì²âtypeÊÇ·ñºÏ·¨
		Set typeSet = MessageType.getSet();
		String s=String.valueOf(str_type);
		Integer type = Integer.valueOf(s);
		if(!typeSet.contains(type))
			return null;
		String msg = str.substring(5);
		return null; 
	}
	public static void main(String[] args) {
		System.out.println(Integer.valueOf("0a12"));
	}
}
