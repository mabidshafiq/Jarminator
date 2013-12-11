package net.sf.jarminator;

import javax.swing.UIManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Jarminator works in both console and GUI mode.
 * This class contains also searching mechanism.
 */
public class Jarminator {

	public static final String VERSION = "v1.0";
	public static HashMap<String, String> jarsWithTimeStamp = new HashMap<String, String>();
	public static DateFormat formatter = new SimpleDateFormat("dd-MMM-yy"); 
	
	public static String EXCLUDE_KEYWORDS = ".*(oracle|apache|bea|ibm|sun|mongodb|javax|\\$|\\.java|META-INF|cisco|w3c|org).*";
	

	  
	
	public static void main(String args[]) {
		System.out.println("\nJarminator " + VERSION + " - Jar Examinator");
		if ((args.length >= 1) && (args[0].equals("-?"))) {
			System.out.println("usage:\n\tjava -jar jarminator [-c] [<source> [<filter>]]");
			System.out.println("options:\n\t-?\t\tthis help");
			System.out.println("\t-c\t\tconsole mode, print results to console");
			System.out.println("\t<source>\tsource path and jars, separated by ';'");
			System.out.println("\t<filter>\tfilter for classes match");
			return;
		}

		// process arguments
		boolean consoleMode = false;
		String root = null;
		String name = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-c")) {
				consoleMode = true;
				continue;
			}
			if (root == null) {
				root = arg;
				continue;
			}
			if (name == null) {
				name = arg;
			}
		}

		if (consoleMode == false) {
			runJarminatorGui(root, name);
			return;
		}

		// CONSOLE mode
		if (root == null) {
			System.out.println("\n+ nothing to do.\n");
			return;
		}

		if (name != null) {
			name = name.toLowerCase();
		}

		System.out.println("\n+ creating jars list...");
		String[] jarList = resolveJarsPaths(root);
		System.out.println("+ done. total jars: " + jarList.length);

		System.out.println("+ searching...");
		int total = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; i < jarList.length; i++) {
			List<ClassFileEntry> classes = null;
			try {
				classes = resolveClassesInJar(jarList[i], name);
			} catch (JarminatorException jex) {
				System.out.println(jex);
			}
			if (classes != null) {
				total += classes.size();
				for (int j = 0; j < classes.size(); j++) {
					System.out.println(jarList[i] + "   " + classes.get(i));
				}
			}
		}
		System.out.println("done. " + total + " classes found in " + (System.currentTimeMillis() - start) + "ms\n");
	}

	// ---------------------------------------------------------------- run gui

	static JarminatorFrame frame;

	/**
	 * Creates Jarminator frame.
	 */
	public static void runJarminatorGui(String source, String filter) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.exit(1);
		}

		frame = new JarminatorFrame();
		if (source != null) {
			frame.rootTextField.setText(source);
			if (filter != null) {
				frame.filterTextField.setText(filter);
			}
		}
		frame.events.onCreate();
		if (source != null) {
			new LoadDialog(source, filter);
		}
	}


	// ---------------------------------------------------------------- create jars list

	public static boolean quitResolving;
	private static String[] jarsPaths;      // cached jar paths

	/**
	 * Returns a string array of founded jars paths. If root is <code>null</code>,
	 * cached data will be returned.
	 */
	public static String[] resolveJarsPaths(String root) {
		if (root == null) {
			return jarsPaths;
		}

		ArrayList jarList = new ArrayList();
		StringTokenizer st = new StringTokenizer(root, ";");
		while (st.hasMoreTokens()) {
			if (quitResolving == true) {
				break;
			}
			String fileName = st.nextToken().trim();
			File file = new File(fileName);
			if (file.exists() == true) {
				if ((file.isFile() == true) && (fileName.endsWith(".jar"))) {
					jarsWithTimeStamp.put(fileName, ""+ formatter.format(new Date( file.lastModified())));
					jarList.add(file.getAbsolutePath());
				} else if (file.isDirectory() == true) {
					createFileList(jarList, file);
				}
			}
		}
		if (jarList.isEmpty()) {
			jarsPaths = null;
			return null;
		}

		jarsPaths = new String[jarList.size()];
		for (int i = 0; i < jarList.size(); i++) {
			jarsPaths[i] = (String) jarList.get(i);
		}
		return jarsPaths;
	}

	/**
	 * Recursivly creates jars list in a folder.
	 */
	private static void createFileList(ArrayList jarList, File folder) {
		File[] childs = folder.listFiles();
		for (int i = 0; i < childs.length; i++) {
			if (quitResolving == true) {
				break;
			}
			if (childs[i].isDirectory() == true) {
				//System.out.println(childs[i].lastModified());
				createFileList(jarList, childs[i]);
			} else {
				
				String filename = childs[i].getAbsolutePath();
				jarsWithTimeStamp.put(filename, ""+ formatter.format(new Date( childs[i].lastModified())));
				if (filename.endsWith(".jar") == true) {
					jarList.add(filename);
				}
			}
		}
	}


	// ---------------------------------------------------------------- find class in jar

	/**
	 * Resolves jar content.
	 */
	@SuppressWarnings("rawtypes")
	public static List<ClassFileEntry>  resolveClassesInJar(String jarName, String name) throws JarminatorException {
		ArrayList classNames = new ArrayList();
		List<ClassFileEntry> classFiles = new ArrayList<ClassFileEntry>();
		
		
		if (name != null) {
			name = name.toLowerCase();
		}
		try {
			JarFile jar = new JarFile(jarName);
			Enumeration jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				if (quitResolving == true) {
					break;
				}
				JarEntry entry = (JarEntry) jarEntries.nextElement();
				
				if (entry.isDirectory() == true) {
					continue;
				}
				
				
				// filter the unwanted classes before the load.. mostly third party classes
				String classFilter = ".*(\\.class).*";
				if(!entry.getName().matches(classFilter)){
					continue;
				}
				if(name !=null && entry.getName().matches(".*(" + name + ").*") ){
					continue;
				}
								
				if(entry.getName().matches(EXCLUDE_KEYWORDS)){
					continue;
				}
				
				// filter ends here
				
				//System.out.println("jar entry time : " + new Date (entry.getTime()));
				
				//if (name == null) {
					classNames.add(entry.getName());
					classFiles.add(new ClassFileEntry(jarName, entry.getName() , formatter.format(new Date (entry.getTime())),entry.getSize()));
//				} else {
//					if (entry.getName().toLowerCase().indexOf(name) != -1) {
//						classNames.add(entry.getName());
//						classFiles.add(new ClassFileEntry(jarName, entry.getName() , formatter.format(new Date (entry.getTime())),entry.getSize()));
//					}
//				}
			}
		} catch (Exception ex) {
			throw new JarminatorException("JAR examination error: " + jarName + '\n' + ex.toString());
		}
		if (classNames.isEmpty()) {
			return null;
		}
		String[] result = new String[classNames.size()];
		for (int i = 0; i < classNames.size(); i++) {
			result[i] = (String) classNames.get(i);
		}
		return classFiles;
	}
	
}
