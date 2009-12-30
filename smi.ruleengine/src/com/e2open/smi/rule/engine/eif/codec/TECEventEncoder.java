package com.e2open.smi.rule.engine.eif.codec;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

import com.e2open.smi.rule.engine.eif.event.Event;


/**
 * A {@link ProtocolEncoder} which encodes a string into a text line which ends
 * with the delimiter.
 * 
 */
public class TECEventEncoder extends ProtocolEncoderAdapter {
	private final AttributeKey ENCODER = new AttributeKey(getClass(), "encoder");

	private final Charset charset;

	private final String delimiter = "\012\001";

	private int maxEventLength = 8 * 1024;

	/**
	 * Creates a new instance with the current default {@link Charset} and
	 * {@link LineDelimiter#UNIX} delimiter.
	 */
	public TECEventEncoder() {
		this(Charset.defaultCharset());
	}

	/**
	 * Creates a new instance with the spcified <tt>charset</tt> and the
	 * specified <tt>delimiter</tt>.
	 */
	public TECEventEncoder(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}

		this.charset = charset;
	}

	/**
	 * Returns the allowed maximum size of the encoded line. If the size of the
	 * encoded line exceeds this value, the encoder will throw a
	 * {@link IllegalArgumentException}. The default value is
	 * {@link Integer#MAX_VALUE}.
	 */
	public int getMaxEventLength() {
		return maxEventLength;
	}

	/**
	 * Sets the allowed maximum size of the encoded line. If the size of the
	 * encoded line exceeds this value, the encoder will throw a
	 * {@link IllegalArgumentException}. The default value is
	 * {@link Integer#MAX_VALUE}.
	 */
	public void setMaxEventLength(int maxEventLength) {
		if (maxEventLength <= 0) {
			throw new IllegalArgumentException("maxLineLength: " + maxEventLength);
		}

		this.maxEventLength = maxEventLength;
	}

	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);
		if (encoder == null) {
			encoder = charset.newEncoder();
			session.setAttribute(ENCODER, encoder);
		}

		Event event = (Event) message;
		String wireFMT = event.toRawFMT();
		
		int msgLen = wireFMT.length();
		int pduLen = msgLen + 36;

		String value = wireFMT;
		IoBuffer buf = IoBuffer.allocate(value.length()+36).setAutoExpand(true);
		buf.putString("<START>>", encoder);
		buf.putInt(0);
		buf.putInt(0);
		buf.putInt(0);
		buf.putInt(0);
		buf.putInt(0);
		buf.putInt(msgLen);
		buf.putInt(pduLen);
		buf.putString(value, encoder);
		if (buf.position() > maxEventLength) {
			throw new IllegalArgumentException("Line length: " + buf.position());
		}
		buf.putString(delimiter, encoder);
		buf.flip();
		out.write(buf);
	}

	public void dispose() throws Exception {
	}
}