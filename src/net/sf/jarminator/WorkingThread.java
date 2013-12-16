package net.sf.jarminator;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


class WorkingThread	extends Thread {

	private final LoadDialog load;
	private final String root;
	private final String name;
	
	Map<String, List<ClassFileEntry>> duplicateFilesMap = new HashMap<String, List<ClassFileEntry>>();
	Set<ClassFileEntry> duplicateClasses = new HashSet<ClassFileEntry>();
	

	WorkingThread(LoadDialog loadDialog, String root, String name) {
		this.load = loadDialog;
		this.root = root;
		this.name = name;
		Jarminator.quitResolving = false;
	}

	public void run() {
		doLoadApply();
	}

	public synchronized void quit() {
		Jarminator.quitResolving = true;
	}

	// ---------------------------------------------------------------- doLoadApply

	/**
	 * Simple alphabetic comparator.
	 */
	private Comparator alphabeticComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	};

	/**
	 * Load or Apply main method.
	 */
	void doLoadApply() {
		long time = System.currentTimeMillis();
		load.loadLabel.setText("Scanning source JARs");
		String[] jars = Jarminator.resolveJarsPaths(root);
		if (jars == null) {
			load.dispose();
			return;
		}
		load.loadLabel.setText("Sorting JARs");
		Arrays.sort(jars, alphabeticComparator);

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(null);		// unvisible root
		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(null);	// unvisible root
		DefaultMutableTreeNode node3 = new DefaultMutableTreeNode(null);	// unvisible root
		
		DefaultMutableTreeNode child;
		int count = 0;
		int errors = 0;
		int jarCount = 0;
		int i;
		
		// allClasses = jarname, class list
		HashMap<String, Set<ClassFileEntry>> allClasses = new HashMap<String, Set<ClassFileEntry>>();
		for (i = 0; i < jars.length; i++) {
			if (Jarminator.quitResolving == true) {
				break;
			}
			load.loadLabel.setText("Examine JAR " + (i + 1) + '/' + jars.length);

			List<ClassFileEntry> classes = null;
			
			try {
				classes = Jarminator.resolveClassesInJar(jars[i], name);
			} catch (JarminatorException jex) {
				errors++;			// count JAR errors
			}
			if (classes != null) {
				String value = jars[i];
				allClasses.put(value, new HashSet(classes));
				/*if (value.startsWith(userDir)) {
					value =	value.substring(userDir.length());
					if (value.charAt(0) == File.separatorChar) {
						value = value.substring(1);
					}
				}*/
				child = new DefaultMutableTreeNode(value);
				node.add(child);
				jarCount++;
				filterJarClasses(value, child, classes);
				filterJarClasses(value, node2, classes);
				count += classes.size();
			}
		}
		
		createDuplicateClassMap(allClasses);		
//		printReportOnConsole();
		printReportOnConsoleCSV();
		creatDupliateClassesTree(node3);
		
	    Jarminator.frame.jarsTree.setModel(new DefaultTreeModel(node));
		
		Jarminator.frame.classesTree.setModel(new DefaultTreeModel(node2));
		
		Jarminator.frame.duplicateClassesTree.setModel(new DefaultTreeModel(node3));
		
		time = System.currentTimeMillis() - time;
		StringBuffer sb = new StringBuffer(200);
		sb.append("<html>Total/Visible jars: ").append(i).append('/').append(jarCount);
		if (errors != 0) {
			sb.append("(errors: ").append(errors).append(") ");
		}
		sb.append("&nbsp; Total files: ").append(count).append("<br>Jarmination time: ").append(time).append("ms.");
		Jarminator.frame.statusLabel.setText(sb.toString());
		load.dispose();
	}

	// ---------------------------------------------------------------- filter jar classes

	private void filterJarClasses(String jarName, DefaultMutableTreeNode node, List<ClassFileEntry> classes) {

		for (int i = 0; i < classes.size(); i++) {

			DefaultMutableTreeNode parent = node;
			ClassFileEntry classEntry = classes.get(i);
			StringTokenizer st = new StringTokenizer(classes.get(i).getClassName(), "/");
			while (st.hasMoreTokens()) {
				String value = st.nextToken();
				boolean lastToken = !st.hasMoreTokens();	// is new element the last

				// [1] examine if children already exist
				boolean exist = false;
				boolean duplicate = false;
				Enumeration e = parent.children();
				while (e.hasMoreElements()) {
					DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) e.nextElement();
					String dtmName = dtm.toString();
					if ((dtmName != null) && (dtmName.equals(value))) {
						if (lastToken == false) {		// allow duplicated leafs
							exist = true;				// so only skip duplicated folders
							parent = dtm;
							break;
						} else {
							duplicate = true;			// duplicate found
							break;
						}
					}
				}

				// [2] add children
				if (exist == false) {
					int index = 0;
					boolean found = false;
					e = parent.children();
					while (e.hasMoreElements()) {
						DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) e.nextElement();
						String dtmName = dtm.toString();
						if (dtmName != null) {
							if (dtm.isLeaf() && !lastToken) {	// current element is leaf and new element is not
								index = parent.getIndex(dtm);	// therefore, insert folder above all leafs
								found = true;
								break;
							}
							if (!dtm.isLeaf() && lastToken) {	// current element is folder and new elemebt is leaf
								continue;						// therefore, skip all folders
							}
							if (alphabeticComparator.compare(dtmName, value) >= 0) {
								index = parent.getIndex(dtm);
								found = true;
								break;
							}
						}
						index++;
					}

					DefaultMutableTreeNode child;
					if (lastToken == false) {
						child = new DefaultMutableTreeNode(value);						// default class for folders
					} else {
						
						child = new DoubleStringTreeNode(value, jarName, duplicate);	// special class for leafs
					}
					if (found == false) {		// place where to insert was not found
						parent.add(child);
					} else {					// insertion place found
						if (duplicate) {		// move duplicates below first
							index++;
						}
						parent.insert(child, index);
						
						if(duplicate == true){
							//System.out.println("Class Name: " + value);
							Enumeration  en = parent.children();
							while (en.hasMoreElements()) {
								
								Object ob = en.nextElement();
								if(ob instanceof DoubleStringTreeNode){
									DoubleStringTreeNode tempObject = (DoubleStringTreeNode)ob;
									String[] userObject = (String[]) tempObject.getUserObject();
									
									Object currentParent =tempObject.getParent();
									while(currentParent !=null){
										Object userObjectTemp = ((DefaultMutableTreeNode)currentParent).getUserObject();
										
										currentParent = ((DefaultMutableTreeNode)currentParent).getParent();
									}
									
									// ADD ENTRY INTO DUPLICATE CLASSES set		
									duplicateClasses.add(classEntry);
																
								}
								
							}

						}
						
					}
					parent = child;
				}
			}
		}
	}


	private void  creatDupliateClassesTree(DefaultMutableTreeNode node){
		
		for (String str : duplicateFilesMap.keySet())
        {
			
			StringBuffer jarNames = new StringBuffer();
			List<ClassFileEntry> classEntries =  duplicateFilesMap.get(str);
			
			for(ClassFileEntry val : classEntries){
				jarNames.append("<br>");
				jarNames.append(val.getJarName());
			}
			
			node.add(new DoubleStringTreeNode(str, jarNames.toString(), false));           
        }
		
	}
	
	private void createDuplicateClassMap(HashMap<String, Set<ClassFileEntry>> allClasses){
		
		Iterator itr = duplicateClasses.iterator();
		
		while(itr.hasNext()){
			ClassFileEntry duplicateEntry = (ClassFileEntry)itr.next();			
			
			for(String jarName: allClasses.keySet()){
				Set<ClassFileEntry> classes = allClasses.get(jarName);
				for(ClassFileEntry e : classes){
					if(e.getClassName().equalsIgnoreCase(duplicateEntry.getClassName())){
						addClassInDuplicateClassesMap(jarName, e.getClassName(), e);
						
					}
				}
				
			}
		}
	}
	
	
	private void addClassInDuplicateClassesMap(String jarName, String className, ClassFileEntry entry){
										
		if(duplicateFilesMap.containsKey(className.toString())){
		
			List<ClassFileEntry> entries = duplicateFilesMap.get(className.toString());
			boolean flag = true;
			for(ClassFileEntry e : entries){
				if(e.jarName.equalsIgnoreCase(jarName)){
					flag = false;
				}
				
			}
			if(flag){
				entries.add(new ClassFileEntry(jarName, className.toString(), entry.getTime().toString(),entry.getSize()));	
			}
			
		}else{
			List<ClassFileEntry> entries = new ArrayList<ClassFileEntry>();
			
			entries.add(new ClassFileEntry(jarName, className.toString(), entry.getTime().toString(),entry.getSize()));
			duplicateFilesMap.put(className.toString(), entries);	
		}
		
	}
	
	private void printReportOnConsoleCSV(){
		// print hash set
		 Iterator itr = duplicateFilesMap.keySet().iterator();
			 while(itr.hasNext()){
				 StringBuffer temp = new StringBuffer();
				 temp.append("\n");
				 String className = (String)itr.next();
				// temp.append(className);
				 
				 //System.out.println(className+ ",");
				 List<ClassFileEntry> values =  duplicateFilesMap.get(className);
				
				 for(ClassFileEntry entry : values){
					 temp.append(entry.getClassName() );
					 temp.append(",");
					 temp.append(entry.getTime());
					 temp.append(",");
					 temp.append(entry.getSize() + "b");
					 temp.append(",");
					 temp.append(entry.getJarName());
					 temp.append(",");
					 JarFileEntry jfe = Jarminator.jarsWithTimeStamp.get(entry.getJarName());
					 temp.append(jfe.getTime());
					 temp.append(",");
					 temp.append(jfe.getSize() + "b");
					// print comma separated string :)
					 System.out.println(temp.toString()); 
					 temp = new StringBuffer();

				 }	
				 
			 }
			 
	}
	
	
	
	private void printReportOnConsole(){
		// print hash set
		 Iterator itr = duplicateFilesMap.keySet().iterator();
			 while(itr.hasNext()){
				 StringBuffer temp = new StringBuffer();
				 temp.append("\n");
				 String className = (String)itr.next();
				// temp.append(className);
				 
				 //System.out.println(className+ ",");
				 List<ClassFileEntry> values =  duplicateFilesMap.get(className);
				
				 for(ClassFileEntry entry : values){
					 temp.append(entry.getClassName() + " "+ entry.getTime());
					 temp.append(" " + entry.getSize() + "b");
					 temp.append("\n");

					 JarFileEntry jfe = Jarminator.jarsWithTimeStamp.get(entry.getJarName());
					 temp.append(jfe.getTime());
					 temp.append(" ");
					 temp.append(jfe.getSize() + "b");
					 
					// print comma separated string :)
					 System.out.println(temp.toString()); 
					 temp = new StringBuffer();

				 }	
				 
			 }
			 
	}
	
	/**
	 * get date a class was compiled by looking at the corresponding class file in the jar.
	 * @author Zig
	*/
	public static Date getCompileTimeStamp( Class<?> cls )  throws IOException 
	{
		
		 ClassLoader loader = cls.getClassLoader();
		   String filename = cls.getName().replace('.', '/') + ".class";
		   // get the corresponding class file as a Resource.
		   URL resource=( loader!=null ) ?
		                loader.getResource( filename ) :
		                ClassLoader.getSystemResource( filename );
		   URLConnection connection = resource.openConnection();
		   // Note, we are using Connection.getLastModified not File.lastModifed.
		   // This will then work both or members of jars or standalone class files.
		   long time = connection.getLastModified();
		   return( time != 0L ) ? new Date( time ) : null;
	}

	
}	


