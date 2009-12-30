package com.e2open.smi.rule.engine.eif.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;



/**
 * A {@link ProtocolCodecFactory} that performs encoding and decoding between
 * an Event wire format and a Java  object.  
 *
 */
public class TECEventCodecFactory implements ProtocolCodecFactory {

    private final TECEventEncoder encoder;
	//    private final TECEventDecoder decoder;
    private final TECEventDecoderV2 decoder;
    /**
     * Creates a new instance with the current default {@link Charset}.
     */
    public TECEventCodecFactory() {
    	this(Charset.defaultCharset());
    }

    /**
     * Creates a new instance with the specifiec Character Set {@link Charset}.
     */
    public TECEventCodecFactory(Charset charset) {
    	charset = Charset.defaultCharset();
        encoder = new TECEventEncoder(charset);
        decoder = new TECEventDecoderV2(charset);
    }

    public ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) {
        return decoder;
    }

    /**
     * Returns the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     * <p>
     * This method does the same job with {@link TECEventEncoder#getMaxEventLength()}.
     */
    public int getEncoderMaxLineLength() {
        return encoder.getMaxEventLength();
    }

    /**
     * Sets the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     * <p>
     * This method does the same job with {@link TECEventEncoder#setMaxEventLength(int)}.
     */
    public void setEncoderMaxLineLength(int maxLineLength) {
        encoder.setMaxEventLength(maxLineLength);
    }

    /**
     * Returns the allowed maximum size of the line to be decoded.
     * If the size of the line to be decoded exceeds this value, the
     * decoder will throw a {@link BufferDataException}.  The default
     * value is <tt>1024</tt> (1KB).
     * <p>
     * This method does the same job with {@link TECEventDecoder#getMaxEventLength()}.
     */
    public int getDecoderMaxEventLength() {
        return decoder.getMaxLineLength();
    }

    /**
     * Sets the allowed maximum size of the line to be decoded.
     * If the size of the line to be decoded exceeds this value, the
     * decoder will throw a {@link BufferDataException}.  The default
     * value is <tt>1024</tt> (1KB).
     * <p>
     * This method does the same job with {@link TECEventDecoder#setMaxEventLength(int)}.
     */
    public void setDecoderMaxEventLength(int maxLineLength) {
        decoder.setMaxLineLength(maxLineLength);
    }
}
