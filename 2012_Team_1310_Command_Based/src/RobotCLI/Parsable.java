package RobotCLI;

public interface Parsable {
    public void parseFrom(String value);
    
    public String toString();
    
    public class ParsableInteger implements Parsable {
    	int value;
    	
    	public ParsableInteger(int value) {
    		this.value = value;
    	}
    	
    	public String toString() {
    		return Integer.toString(value);
    	}
    	
		public void parseFrom(String value) {
			this.value = Integer.parseInt(value);
		}
		
		public int get() {
			return value;
		}

		public void set(int value) {
			this.value = value;
		}
    }
    
    public class ParsableDouble implements Parsable {
    	double value;
    	
    	public ParsableDouble(double value) {
    		this.value =value;
    	}
    	
    	public String toString() {
    		return Double.toString(value);
    	}
    	

		public void parseFrom(String value) {
			this.value = Double.parseDouble(value);
		}
		
		public double get() {
			return value;
		}

		public void set(double value) {
			this.value = value;
		}
    }
    
    public class ParsableString implements Parsable {
    	String value;
    	
    	public ParsableString(String value) {
    		this.value = value;
    	}
    	

    	public String toString() {
    		return value;
    	}
    	

		public void parseFrom(String value) {
			this.value = value;
		}
		
		public String get() {
			return value;
		}

		public void set(String value) {
			this.value = value;
		}
    }
}
