package com.crawler.tool;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.htoou.util.DoubleUtil;
import com.htoou.util.RegularUtil;
import com.htoou.util.StringUtil;

/**
 * 域名工具
 * 
 * @author sunny
 * 
 */
public class DomainUtil {

	/**
	 * 顶级域名
	 */
	private static final String TOP_DOMAIN = "/^aero$|^cat$|^coop$|^int$|^museum$|^pro$|^travel$|^xxx$|^com$|^tel$|^mobi$|^me$|^net$|^gov$|^org$|^mil$|^edu$|^biz$|^info$|^name$|^ac$|^mil$|^co$|^ed$|^gv$|^nt$|^bj$|^hz$|^sh$|^tj$|^cq$|^he$|^nm$|^ln$|^jl$|^hl$|^js$|^zj$|^ah$|^hb$|^hn$|^gd$|^gx$|^hi$|^sc$|^gz$|^yn$|^xz$|^sn$|^gs$|^qh$|^nx$|^xj$|^tw$|^hk$|^mo$|^fj$|^ha$|^jx$|^sd$|^sx$/i";

	/**
	 * 国家域名
	 */
	private static final String CITY_DOMAIN = "/^ac$|^ad$|^ae$|^af$|^ag$|^ai$|^al$|^am$|^an$|^ao$|^aq$|^ar$|^as$|^at$|^au$|^aw$|^az$|^ba$|^bb$|^bd$|^be$|^bf$|^bg$|^bh$|^bi$|^bj$|^bm$|^bo$|^br$|^bs$|^bt$|^bv$|^bw$|^by$|^bz$|^ca$|^cc$|^cd$|^cf$|^cg$|^ch$|^ci$|^ck$|^cl$|^cm$|^cn$|^co$|^cr$|^cs$|^cu$|^cv$|^cx$|^cy$|^cz$|^de$|^dj$|^dk$|^dm$|^do$|^dz$|^ec$|^ee$|^eg$|^eh$|^er$|^es$|^et$|^eu$|^fi$|^fj$|^fk$|^fm$|^fo$|^fr$|^ly$|^hk$|^hm$|^hn$|^hr$|^ht$|^hu$|^id$|^ie$|^il$|^im$|^in$|^io$|^ir$|^is$|^it$|^je$|^jm$|^jo$|^jp$|^ke$|^kg$|^kh$|^ki$|^km$|^kn$|^kp$|^kr$|^kw$|^ky$|^kz$|^la$|^lb$|^lc$|^li$|^lk$|^lr$|^ls$|^lt$|^lu$|^lv$|^ly$|^ga$|^gb$|^gd$|^ge$|^gf$|^gg$|^gh$|^gi$|^gl$|^gm$|^gn$|^gp$|^gq$|^gr$|^gs$|^gt$|^gu$|^gw$|^gy$|^ma$|^mc$|^md$|^mg$|^mh$|^mk$|^ml$|^mm$|^mn$|^mo$|^mp$|^mq$|^mr$|^ms$|^mt$|^mu$|^mv$|^mw$|^mx$|^my$|^mz$|^na$|^nc$|^ne$|^nf$|^ng$|^ni$|^nl$|^no$|^np$|^nr$|^nu$|^nz$|^om$|^re$|^ro$|^ru$|^rw$|^pa$|^pe$|^pf$|^pg$|^ph$|^pk$|^pl$|^pm$|^pr$|^ps$|^pt$|^pw$|^py$|^qa$|^wf$|^ws$|^sa$|^sb$|^sc$|^sd$|^se$|^sg$|^sh$|^si$|^sj$|^sk$|^sl$|^sm$|^sn$|^so$|^sr$|^st$|^su$|^sv$|^sy$|^sz$|^tc$|^td$|^tf$|^th$|^tg$|^tj$|^tk$|^tm$|^tn$|^to$|^tp$|^tr$|^tt$|^tv$|^tw$|^tz$|^ua$|^ug$|^uk$|^um$|^us$|^uy$|^uz$|^va$|^vc$|^ve$|^vg$|^vi$|^vn$|^vu$|^ye$|^yt$|^yu$|^za$|^zm$|^zr$|^zw$/i";

	/**
	 * IP地址
	 * 
	 * @param host
	 * @return
	 */

	private static final String IP_DOMAIN = "^[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*$";

	/**
	 * 获得带www的域名网站
	 * 
	 * @param host
	 *            uri
	 * @return
	 */
	public static String getDomainByUri(String host) {
		if (host == null) {
			return host;
		}
		String domain = host.replaceAll("^www\\.", "");
		String[] args = domain.split("\\.");
		int len = args.length;
		if (len == 3) {
			if (isTOPDomain(args[1]) && isCITYDomain(args[2])) {
				domain = "www." + args[0] + "." + args[1] + "." + args[2];
			} else {
				domain = "www." + args[1] + "." + args[2];
			}
		} else if (len >= 3) {
			if (isIPHost(domain)) {
				return domain;
			}
			if (isTOPDomain(args[len - 2]) && isCITYDomain(args[len - 1])) {
				domain = "www." + args[len - 3] + "." + args[len - 2] + "." + args[len - 1];
			} else {
				domain = "www." + args[len - 2] + "." + args[len - 1];
			}
		} else {
			domain = "www." + domain;
		}
		return domain;
	}

	/**
	 * 判断域名是否为主域名
	 * 
	 * @param host
	 * @return
	 */
	public static boolean isDomainHost(String host) {
		if (host == null) {
			return false;
		}
		String domain = host.replaceAll("^www\\.", "");
		String[] args = domain.split("\\.");
		int len = args.length;
		if (len == 3) {
			if (isTOPDomain(args[1]) && isCITYDomain(args[2])) {
				return true;
			} else {
				return false;
			}
		} else if (len >= 3) {
			if (isIPHost(domain)) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 返回主域名
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomainByUrl(String url) {
		String uri = RegularUtil.getDomainOffWWW(url);
		return getDomainByUri(uri);
	}

	/**
	 * 返回可能是主域名，也有可能是二级域名
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomainOffWWW(String url) {
		if (url == null) {
			return url;
		}
		String host = RegularUtil.getDomainOffWWW(url);
		String domain = host;
		String[] args = host.split("\\.");
		int len = args.length;
		if (len == 3) {
			if (isTOPDomain(args[1]) && isCITYDomain(args[2])) {
				domain = args[0] + "." + args[1] + "." + args[2];
			} else {
				domain = args[1] + "." + args[2];
			}
		} else if (len >= 3) {
			if (isIPHost(domain)) {
				return domain;
			}
			if (isTOPDomain(args[len - 2]) && isCITYDomain(args[len - 1])) {
				domain = args[len - 3] + "." + args[len - 2] + "." + args[len - 1];
			} else {
				domain = args[len - 2] + "." + args[len - 1];
			}
		}
		return domain;
	}

	/**
	 * 返回带http的domain
	 */
	public static String getDomainWithHttp(String url) {
		String uri = RegularUtil.getDomainOffWWW(url);
		return "http://" + getDomainByUri(uri);
	}

	private static boolean isTOPDomain(String s) {
		Pattern pattern = Pattern.compile(TOP_DOMAIN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	private static boolean isCITYDomain(String s) {
		Pattern pattern = Pattern.compile(CITY_DOMAIN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	private static boolean isIPHost(String s) {
		Pattern pattern = Pattern.compile(IP_DOMAIN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	public static double getURLSimilar(String url1, String url2) {
		double similar = 0;
		final double TOTAL_SCORE = 50d;
		try {
			List<String> uris1 = getURIStruct(url1);
			List<String> uris2 = getURIStruct(url2);
			for (int i = 0; i < uris1.size() && i < uris2.size(); i++) {
				if (StringUtil.equalsIgnoreCase(uris1.get(i), uris2.get(i))) {
					similar += DoubleUtil.div(TOTAL_SCORE, (i * 2 + 1), 2);
				}
			}
		} catch (MalformedURLException e) {
		} catch (URISyntaxException e) {
		}
		return similar;
	}

	private static List<String> getURIStruct(String url) throws MalformedURLException, URISyntaxException {
		URL http = new URL(url);
		Pattern STRUCT_PATTERN = Pattern.compile("\\/([^\\/]+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = STRUCT_PATTERN.matcher(http.toURI().getPath());
		List<String> stucts = new ArrayList<String>();
		stucts.add(http.getHost());
		while (matcher.find()) {
			stucts.add(matcher.group(1));
		}
		return stucts;
	}

	public static List<String> getURI(String url, List<String> lst) {

		Pattern pattern = Pattern.compile(".*?\\.(.*\\..*)");
		Matcher matcher = pattern.matcher(url);
		lst.add(url);
		if (matcher.find()) {
			return getURI(matcher.group(1), lst);
		}
		return lst;
	}
	
	public static void main(String[] args) {
		System.out.println(DomainUtil.getDomainByUrl("http://bj.qiyexinyong.org/"));
	}
}
