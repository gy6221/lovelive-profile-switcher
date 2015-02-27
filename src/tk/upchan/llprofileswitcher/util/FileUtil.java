package tk.upchan.llprofileswitcher.util;

import tk.upchan.llprofileswitcher.exception.RootAccessException;

public class FileUtil {
	public static String cp(String src, String dst) throws RootAccessException{
		StringBuilder sb = new StringBuilder();
		sb.append("cat");
		sb.append(" ");
		sb.append("'"+src+"'");
		sb.append(" > ");
		sb.append("'"+dst+"'");
		return CommandUtil.runAsRoot(sb.toString());
	}
	
	public static String rm(String target) throws RootAccessException{
		StringBuilder sb = new StringBuilder();
		sb.append("rm -f");
		sb.append(" ");
		sb.append("'"+target+"'");
		return CommandUtil.runAsRoot(sb.toString());
	}
}
