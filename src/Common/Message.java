package Common;

import java.io.Serializable;
import java.util.Scanner;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	private int num;//消息序号
	private String message;//消息
	private int type;//消息类型
	private Topic topic;//消息主题

	//构造函数
	public Message() {}
	public Message(String s,Topic topic,int num) {
		// 默认消息类型为1
		this.setType(MessageType.REPLY_EXPECTED);
		this.setNum(num);
		if(s.length()>9999) {
//			this.length = 9999;
			this.message = s.substring(0, 9999);
		}
		else{
			this.message = s;
//			this.length = s.length();
		}
		this.topic = topic;
	}
	//构造函数
	public Message(String s,int type,int num) {
		this.setType(type);
		this.setNum(num);
		if(s.length()>9999) {
//			this.length = 9999;
			this.message = s.substring(0, 9999);
		}
		else{
			this.message = s;
//			this.length = s.length();
		}
	}
	public Message(String s,int type,Topic topic,int num) {
		this.setType(type);
		this.topic = topic;
		this.setNum(num);
		if(s.length()>9999) {
//			this.length = 9999;
			this.message = s.substring(0, 9999);
		}
		else{
			this.message = s;
//			this.length = s.length();
		}
		
	}
	public String getMessage() {
		return message;
	}
//	public String getJointMessage() {
//		System.out.println(this.length);
//		String str_len = String.format("%04d",this.length);
//		String str_type = String.valueOf(this.type);
//		String JointMessage = str_len+str_type+message;
//		//System.out.println(JointMessage);
//		return JointMessage;
//	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		if(MessageType.contains(type))
			this.type = type;
		else// 默认消息类型为1
			this.setType(MessageType.REPLY_EXPECTED);
	}

//	public int getLength() {
//		return length;
//	}
//	
	public static void main(String[] args) {
//		System.out.println("请输入要发送的数据：");
//		Scanner sc = new Scanner(System.in);
        //利用hasNextXXX()判断是否还有下一输入项
//        while (sc.hasNext()) {
//            //利用nextXXX()方法输出内容
//            String s = sc.next();
//            Message message = new Message(s);
//            String joint = message.getJointMessage();
//            System.out.println(joint);
//        }
		
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}

