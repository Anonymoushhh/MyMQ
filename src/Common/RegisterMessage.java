package Common;

import java.io.Serializable;

public class RegisterMessage extends Message implements Serializable{

	private IpNode ipNode;
	private String message;
	private int num;
	private static final int type = MessageType.REGISTER;
	public RegisterMessage(IpNode ipNode,String message,int num) {
		this.num = num;
		this.ipNode = ipNode;
		this.message = message;
	}
	public IpNode getIpNode() {
		return ipNode;
	}
	public int getNum() {
		return num;
	}
	public int getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}


}
