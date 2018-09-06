public class MyWritable implements Writable {
	
	private int num = 1;
	private String data;
	public MyWritable() {
		// TODO Auto-generated constructor stub
	}
	public MyWritable(int num, String data){
		this.num = num;
		this.data = data;
	}
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(num);
		out.writeUTF(data);
	}
 
 
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		num = in.readInt();
		data = in.readUTF();
	}
	public int getNum() {
		return num;
	}
 
	public void setNum(int num) {
		this.num = num;
	}
 
	public String getData() {
		return data;
	}
 
	public void setData(String data) {
		this.data = data;
	}
 
}
