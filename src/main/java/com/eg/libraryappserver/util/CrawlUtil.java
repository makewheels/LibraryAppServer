package com.eg.libraryappserver.util;

public class CrawlUtil {
	private static long lastRequestTime = -1;

	/**
	 * 有频率限制的get请求
	 * 
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		if (lastRequestTime != -1) {
			long diff = System.currentTimeMillis() - lastRequestTime;
			if (diff <= Constants.WAIT_TIME_MILLIS) {
				try {
					long waitMillis = Constants.WAIT_TIME_MILLIS - diff;
					System.out.println("wait for " + waitMillis + " ms");
					Thread.sleep(waitMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
		return HttpUtil.get(url);
	}

}
