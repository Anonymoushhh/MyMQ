package Utils;


public class SequenceUtil {

	private int count = 0;
	public synchronized int getSequence() {
		return count++;
	} 
//	public static void main(String[] args) {
//        //ʹ���߳�ģ���û� ��������
//		SequenceUtil sequenceUtil = new SequenceUtil();
//        for (int i = 0; i < 50000; i++) {
//            new Thread(){
//                public void run() {
//                    System.out.println(sequenceUtil.getSequence());
//                };
//            }.start();
//        }
//
//	}

}
