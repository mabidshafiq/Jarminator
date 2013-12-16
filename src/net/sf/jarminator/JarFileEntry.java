package net.sf.jarminator;

public class JarFileEntry {

	String name;
	String time;
	long size;

	public JarFileEntry(String name, String time, long size) {
		super();
		this.name = name;
		this.time = time;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
