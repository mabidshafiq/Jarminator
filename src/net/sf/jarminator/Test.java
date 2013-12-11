package net.sf.jarminator;

public class Test {

	public static void main(String [] args){
		
		String regExp = ".*(one|mine).*";
		
		Boolean result = "this  is  what is yours".matches(regExp);
		System.out.println("result > " + result);
	}
}
