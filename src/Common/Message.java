package Common;

import java.util.Scanner;

public class Message {

	private String message;//消息
	private int type;//消息类型
	private int length;//消息长度

	//构造函数
	public Message(String s) {
		// 默认消息类型为0
		this.setType(MessageType.ONE_WAY);
		if(s.length()>9999) {
			this.length = 9999;
			this.message = s.substring(0, 9999);
		}
		else{
			this.message = s;
			this.length = s.length();
		}
	}
	
	public Message(String s,int type) {
		this.type = type;
		if(s.length()>9999) {
			this.length = 9999;
			this.message = s.substring(0, 9999);
		}
		else{
			this.message = s;
			this.length = s.length();
		}
		
	}
	
	public String getJointMessage() {
		System.out.println(this.length);
		String str_len = String.format("%04d",this.length);
		String str_type = String.valueOf(this.type);
		String JointMessage = str_len+str_type+message;
		//System.out.println(JointMessage);
		return JointMessage;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		if(type==0||type==1||type==2)
			this.type = type;
		else
			System.out.println("设置失败。");
	}

	public int getLength() {
		return length;
	}
	
	public static void main(String[] args) {
		System.out.println("请输入要发送的数据：");
		Scanner sc = new Scanner(System.in);
        //利用hasNextXXX()判断是否还有下一输入项
        while (sc.hasNext()) {
            //利用nextXXX()方法输出内容
            String s = sc.next();
            Message message = new Message(s);
            String joint = message.getJointMessage();
            System.out.println(joint);
        }
		
	}
}

