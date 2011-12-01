package de.timroes.dokuapp.util;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author timroes
 */
public class RegexReplace {
	
	private final Pattern pattern;
	
	public RegexReplace(String regex) {
		pattern = Pattern.compile(regex);
	}

	public String replaceAll(String input, Callback callback) {

		final Matcher matcher = pattern.matcher(input);

		int startAt = 0;
		while(matcher.find(startAt)) {
			final MatchResult res = matcher.toMatchResult();
			final String replace = callback.replace(res);
			input = input.substring(0, res.start()) + replace + input.substring(res.end());
			matcher.reset(input);
			startAt = res.start() + 1;
		}

		return input;
		
	}


	public static interface Callback {

		public String replace(MatchResult match);
		
	}
	
}
