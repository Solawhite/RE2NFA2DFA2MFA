package simple;

public class Cell {
	public static final int EPSILON = -1;
	public static final int EMPTY = -2; 

	private int edge;

	public int getEdge() {
		return edge;
	}

	public void setEdge(int type) {
		edge = type;
	}

	public Cell next; 
	public Cell next2; 
	private int state;
	private boolean visited = false;
	
	public void setVisited() {
		visited = true;
	}

	public void setUnVisited() {
		visited = false;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setState(int num) {
		state = num;
	}

	public int getState() {
		return state;
	}


	public void clearState() {
    	next = next2 = null;
    	state = -1;		
	}
	
	@Override
	public String toString() {
		return (char)edge+" "+state+""+isVisited();
	}
}
