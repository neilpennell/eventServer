package com.e2open.smi.rule.engine.eif.codec;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.e2open.smi.rule.engine.eif.event.Event;

public class TECEventDecoderV2 extends CumulativeProtocolDecoder {
	private CharsetDecoder charsetDecoder;
	private static final int HEADER_LENGTH = 36;

	public TECEventDecoderV2(Charset charset) {
		this.charsetDecoder = charset.newDecoder();
	}

	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int start = in.position();

		byte p1 = 0, p2 = 0, p3 = 0, p4 = 0;
		while (in.hasRemaining()) {
			byte current = in.get();

			// END\n001
			if (p4 == 'E' && p3 == 'N' && p2 == 'D' && p1 == '\n' && current == '\001') {
				// Remember the current position and limit.
				int position = in.position();
				int limit = in.limit();
				try {
					in.position(start + HEADER_LENGTH);
					in.limit(position);
					// The bytes between in.position() and in.limit()
					IoBuffer t = in.slice();
					String str = t.getString(charsetDecoder).trim();
					writeEvent(session, str, out);
				} finally {
					// Set the position to point right after the
					// detected line and set the limit to the old
					// one.
					in.position(position);
					in.limit(limit);
				}
				// Decoded one line; CumulativeProtocolDecoder will
				// call me again until I return false. So just
				// return true until there are no more lines in the
				// buffer.
				return true;
			}
			p4 = p3;
			p3 = p2;
			p2 = p1;
			p1 = current;
		}

		// Could not find delim in the buffer. Reset the initial
		// position to the one we recorded above.
		in.position(start);

		return false;
	}

	/**
	 * By default, this method propagates the decoded event to {@code
	 * ProtocolDecoderOutput#write(Object)}. You may override this method to
	 * modify the default behavior.
	 * 
	 * @param session
	 *            the {@code IoSession} the received data.
	 * @param wireFMT
	 *            the decoded event in wire format
	 * @param out
	 *            the upstream {@code ProtocolDecoderOutput}.
	 * @throws RecoverableProtocolDecoderException
	 */
	private static final String eventAttributeRegEx = "(.*?);(.*)END$";
	private static final String attributeRegEx = "([^;=]+)\\s*?=\\s*?([^'\"]+?);|([^;=]+)\\s*?=\\s*?\"([^\"]+?)\";|([^;=]+)\\s*?=\\s*?'([^']+?)';";
	private static final Pattern compiledEventAttributeRegEx = Pattern.compile(eventAttributeRegEx);
	private static final Pattern compiledAttributeRegEx = Pattern.compile(attributeRegEx);

	protected void writeEvent(IoSession session, String wireFMT, ProtocolDecoderOutput out) {
		String inputStr;
		inputStr = wireFMT;
		Matcher matcher = compiledEventAttributeRegEx.matcher(inputStr);
		boolean matchFound = matcher.find();

		if (matchFound) {
			Event event = new Event();
			String attrs = "", type = "";
			type = matcher.group(1).trim();
			attrs = matcher.group(2).trim();

			event.setType(type);
			do {
				matcher = compiledAttributeRegEx.matcher(attrs);
				matchFound = matcher.find();

				String key = "", value = "";
				if (matchFound) {
					key = matcher.group(1);
					value = matcher.group(2);
					if (key == null) {
						key = matcher.group(3);
						value = matcher.group(4);
						if (key == null) {
							key = matcher.group(5);
							value = matcher.group(6);
						}
					}
					key = key.trim();
					value = value.trim();
					attrs = attrs.substring(matcher.end());
					event.addAttribute(key, value);
				}
			} while (matchFound && attrs.length() > 0);
			out.write(event);
		}
	}

	public int getMaxLineLength() {
		return 0;
	}

	public void setMaxLineLength(int maxLineLength) {
	}

}
