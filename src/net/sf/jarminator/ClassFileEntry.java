package net.sf.jarminator;

public class ClassFileEntry {

	String jarName;
	String className;
	String time;
	long size;
		

	public ClassFileEntry() {

	}

	public ClassFileEntry(String jarName, String className, String time, long size) {
		super();
		this.jarName = jarName;
		this.className = className;
		this.time = time;
		this.size = size;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
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
