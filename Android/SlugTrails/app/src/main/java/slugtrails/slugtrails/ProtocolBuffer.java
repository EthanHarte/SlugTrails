package slugtrails.slugtrails;

import java.nio.ByteBuffer;

/**
 * A resizable buffer implementation backed by a byte buffer, that is used for
 * reading and writing data.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class ProtocolBuffer {

    /** All of the bit masks. */
    private static final int[] BIT_MASK = { 0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f,
            0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff,
            0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff,
            0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff,
            0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };

    /** The default capacity of this buffer. */
    private static final int DEFAULT_CAP = 128;

    /** The backing byte buffer. */
    private ByteBuffer buf;


    /** The position of the buffer when a variable length packet is created. */
    private int variableLengthPos = 0;

    /** The current bit position. */
    private int bitPosition = 0;

    /**
     * An enum whose values represent the possible order in which bytes are
     * written in a multiple-byte value. Also known as "endianness".
     * 
     * @author blakeman8192
     */
    public enum ByteOrder {
        LITTLE,
        BIG,
        MIDDLE,
        INVERSE_MIDDLE
    }

    /**
     * An enum whose values represent the possible custom RuneScape value types.
     * Type <code>A</code> is to add 128 to the value, type <code>C</code> is to
     * invert the value, and type <code>S</code> is to subtract the value from
     * 128. Of course, <code>STANDARD</code> is just the normal data value.
     * 
     * @author blakeman8192
     */
    public enum ValueType {
        STANDARD,
        A,
        C,
        S
    }

    /**
     * Creates a new {@link ProtocolBuffer} with the specified backing byte
     * buffer.
     * 
     * @param buf
     *            the backing byte buffer.
     */
    public ProtocolBuffer(ByteBuffer buf) {
        this.buf = buf;
    }


    public ProtocolBuffer(int cap) {
        this(ByteBuffer.allocate(cap));
    }

    /** Creates a new {@link ProtocolBuffer} with the default capacity. */
    public ProtocolBuffer() {
        this(DEFAULT_CAP);
    }

    /** Prepares the buffer for writing bits. */
    public void startBitAccess() {
        bitPosition = buf.position() * 8;
    }

    /** Prepares the buffer for writing bytes. */
    public void finishBitAccess() {
        buf.position((bitPosition + 7) / 8);
    }

    /**
     * Checks if the buffer can hold the amount of requested bytes. If the
     * buffer cannot hold the specified amount, it will double in size until it
     * is able to.
     * 
     * @param requested
     *            the amount of requested bytes.
     */
    private void requestSpace(int requested) {
        if ((buf.position() + requested + 1) >= buf.capacity()) {
            int oldPosition = buf.position();
            byte[] oldBuffer = buf.array();
            int newLength = (buf.capacity() * 2);
            buf = ByteBuffer.allocate(newLength);
            buf.position(oldPosition);
            System.arraycopy(oldBuffer, 0, buf.array(), 0, oldBuffer.length);
            requestSpace(requested);
        }
    }


    /**
     * Builds a new packet header for a variable length packet. Note that the
     * corresponding <code>endVar()</code> method must be called to finish the
     * packet.
     * 
     * @param opcode
     *            the opcode of the packet
     * @return this protocol buffer.
     */
    public ProtocolBuffer buildVar(int opcode) {
        build(opcode);
        variableLengthPos = buf.position();
        writeByte(0);
        return this;
    }

    public ProtocolBuffer build(int opcode) {
        writeByte(opcode);
        return this;
    }

    /**
     * Builds a new packet header for a variable length packet, where the length
     * is written as a short instead of a byte. Note that the corresponding
     * <code>endVarShort()</code> method must be called to finish the packet.
     * 
     * @param opcode
     *            the opcode of the packet.
     * @return this protocol buffer.
     */
    public ProtocolBuffer buildVarShort(int opcode) {
        build(opcode);
        variableLengthPos = buf.position();
        writeShort(0);
        return this;
    }

    /**
     * Finishes a variable packet header by writing the actual packet length at
     * the length byte's position. Call this when the construction of the actual
     * variable length packet is complete.
     * 
     * @return this protocol buffer.
     */
    public ProtocolBuffer endVar() {
        requestSpace(1);
        buf.put(variableLengthPos,
            (byte) (buf.position() - variableLengthPos - 1));
        return this;
    }

    /**
     * Finishes a variable packet header by writing the actual packet length at
     * the length short's position. Call this when the construction of the
     * variable short length packet is complete.
     * 
     * @return this protocol buffer.
     */
    public ProtocolBuffer endVarShort() {
        requestSpace(2);
        buf.putShort(variableLengthPos,
            (short) (buf.position() - variableLengthPos - 2));
        return this;
    }

    /**
     * Writes the bytes from the argued buffer into this buffer. This method
     * does not modify the argued buffer, and please do not flip the buffer
     * beforehand.
     * 
     * @param from
     *            the argued buffer that bytes will be written from.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeBytes(ByteBuffer from) {
        for (int i = 0; i < from.position(); i++) {
            writeByte(from.get(i));
        }
        return this;
    }

    /**
     * Writes the bytes from the argued buffer into this buffer.
     * 
     * @param from
     *            the argued buffer that bytes will be written from.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeBytes(byte[] from, int size) {
        requestSpace(size);
        buf.put(from, 0, size);
        return this;
    }

    /**
     * Writes the bytes from the argued byte array into this buffer, in reverse.
     * 
     * @param data
     *            the data to write.
     */
    public ProtocolBuffer writeBytesReverse(byte[] data) {
        for (int i = data.length - 1; i >= 0; i--) {
            writeByte(data[i]);
        }
        return this;
    }

    /**
     * Writes the value as a variable amount of bits.
     * 
     * @param amount
     *            the amount of bits to write.
     * @param value
     *            the value of the bits.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeBits(int amount, int value) {

        // Check for invalid amounts.
        if (amount < 0 || amount > 32) {
            throw new IllegalArgumentException(
                "Number of bits must be between 1 and 32 inclusive.");
        }

        // Modify the bit and byte positions.
        int bytePos = bitPosition >> 3;
        int bitOffset = 8 - (bitPosition & 7);

        bitPosition = bitPosition + amount;

        // Re-size the buffer if need be.
        int requiredSpace = bytePos - buf.position() + 1;
        requiredSpace += (amount + 7) / 8;
        if (buf.remaining() < requiredSpace) {
            ByteBuffer old = buf;
            buf = ByteBuffer.allocate(old.capacity() + requiredSpace);
            old.flip();
            buf.put(old);
        }

        // Write the bits.
        for (; amount > bitOffset; bitOffset = 8) {
            byte tmp = buf.get(bytePos);
            tmp &= ~BIT_MASK[bitOffset];
            tmp |= (value >> (amount - bitOffset)) & BIT_MASK[bitOffset];
            buf.put(bytePos++, tmp);
            amount -= bitOffset;
        }
        if (amount == bitOffset) {
            byte tmp = buf.get(bytePos);
            tmp &= ~BIT_MASK[bitOffset];
            tmp |= value & BIT_MASK[bitOffset];
            buf.put(bytePos, tmp);
        } else {
            byte tmp = buf.get(bytePos);
            tmp &= ~(BIT_MASK[amount] << (bitOffset - amount));
            tmp |= (value & BIT_MASK[amount]) << (bitOffset - amount);
            buf.put(bytePos, tmp);
        }
        return this;
    }

    /**
     * Writes a boolean bit flag.
     * 
     * @param flag
     *            the flag to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeBit(boolean flag) {
        writeBits(1, flag ? 1 : 0);
        return this;
    }

    /**
     * Writes a value as a byte.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeByte(int value, ValueType type) {
        requestSpace(1);
        switch (type) {
        case A:
            value += 128;
            break;
        case C:
            value = -value;
            break;
        case S:
            value = 128 - value;
            break;
        case STANDARD:
            break;
        }
        buf.put((byte) value);
        return this;
    }

    /**
     * Writes a value as a normal byte.
     * 
     * @param value
     *            the value to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeByte(int value) {
        writeByte(value, ValueType.STANDARD);
        return this;
    }

    /**
     * Writes a value as a short.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeShort(int value, ValueType type, ByteOrder order) {
        switch (order) {
        case BIG:
            writeByte(value >> 8);
            writeByte(value, type);
            break;
        case MIDDLE:
            throw new IllegalArgumentException(
                "Middle-endian short is impossible!");
        case INVERSE_MIDDLE:
            throw new IllegalArgumentException(
                "Inverse-middle-endian short is impossible!");
        case LITTLE:
            writeByte(value, type);
            writeByte(value >> 8);
            break;
        }
        return this;
    }

    /**
     * Writes a value as a normal big-endian short.
     * 
     * @param value
     *            the value to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeShort(int value) {
        writeShort(value, ValueType.STANDARD, ByteOrder.BIG);
        return this;
    }

    /**
     * Writes a value as a big-endian short.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeShort(int value, ValueType type) {
        writeShort(value, type, ByteOrder.BIG);
        return this;
    }

    /**
     * Writes a value as a standard short.
     * 
     * @param value
     *            the value to write.
     * @param order
     *            the byte order.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeShort(int value, ByteOrder order) {
        writeShort(value, ValueType.STANDARD, order);
        return this;
    }

    /**
     * Writes a value as an integer.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeInt(int value, ValueType type, ByteOrder order) {
        switch (order) {
        case BIG:
            writeByte(value >> 24);
            writeByte(value >> 16);
            writeByte(value >> 8);
            writeByte(value, type);
            break;
        case MIDDLE:
            writeByte(value >> 8);
            writeByte(value, type);
            writeByte(value >> 24);
            writeByte(value >> 16);
            break;
        case INVERSE_MIDDLE:
            writeByte(value >> 16);
            writeByte(value >> 24);
            writeByte(value, type);
            writeByte(value >> 8);
            break;
        case LITTLE:
            writeByte(value, type);
            writeByte(value >> 8);
            writeByte(value >> 16);
            writeByte(value >> 24);
            break;
        }
        return this;
    }

    /**
     * Writes a value as a standard big-endian integer.
     * 
     * @param value
     *            the value to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeInt(int value) {
        writeInt(value, ValueType.STANDARD, ByteOrder.BIG);
        return this;
    }

    /**
     * Writes a value as a big-endian integer.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeInt(int value, ValueType type) {
        writeInt(value, type, ByteOrder.BIG);
        return this;
    }

    /**
     * Writes a value as a standard integer.
     * 
     * @param value
     *            the value to write.
     * @param order
     *            the byte order.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeInt(int value, ByteOrder order) {
        writeInt(value, ValueType.STANDARD, order);
        return this;
    }

    /**
     * Writes a value as a long.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeLong(long value, ValueType type, ByteOrder order) {
        switch (order) {
        case BIG:
            writeByte((int) (value >> 56));
            writeByte((int) (value >> 48));
            writeByte((int) (value >> 40));
            writeByte((int) (value >> 32));
            writeByte((int) (value >> 24));
            writeByte((int) (value >> 16));
            writeByte((int) (value >> 8));
            writeByte((int) value, type);
            break;
        case MIDDLE:
            throw new UnsupportedOperationException(
                "Middle-endian long is not implemented!");
        case INVERSE_MIDDLE:
            throw new UnsupportedOperationException(
                "Inverse-middle-endian long is not implemented!");
        case LITTLE:
            writeByte((int) value, type);
            writeByte((int) (value >> 8));
            writeByte((int) (value >> 16));
            writeByte((int) (value >> 24));
            writeByte((int) (value >> 32));
            writeByte((int) (value >> 40));
            writeByte((int) (value >> 48));
            writeByte((int) (value >> 56));
            break;
        }
        return this;
    }

    /**
     * Writes a value as a standard big-endian long.
     * 
     * @param value
     *            the value to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeLong(long value) {
        writeLong(value, ValueType.STANDARD, ByteOrder.BIG);
        return this;
    }

    /**
     * Writes a value as a big-endian long.
     * 
     * @param value
     *            the value to write.
     * @param type
     *            the value type.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeLong(long value, ValueType type) {
        writeLong(value, type, ByteOrder.BIG);
        return this;
    }

    /**
     * Writes a value as a standard long.
     * 
     * @param value
     *            the value to write.
     * @param order
     *            the byte order to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeLong(long value, ByteOrder order) {
        writeLong(value, ValueType.STANDARD, order);
        return this;
    }

    /**
     * Writes a RuneScape string value.
     * 
     * @param string
     *            the string to write.
     * @return this protocol buffer.
     */
    public ProtocolBuffer writeString(String string) {
        for (byte value : string.getBytes()) {
            writeByte(value);
        }
        writeByte(10);
        return this;
    }

    /**
     * Reads a value as a byte.
     * 
     * @param signed
     *            if the byte is signed.
     * @param type
     *            the value type.
     * @return the value of the byte.
     */
    public int readByte(boolean signed, ValueType type) {
        int value = buf.get();
        switch (type) {
        case A:
            value = value - 128;
            break;
        case C:
            value = -value;
            break;
        case S:
            value = 128 - value;
            break;
        case STANDARD:
            break;
        }
        return signed ? value : value & 0xff;
    }

    /**
     * Reads a standard signed byte.
     * 
     * @return the value of the byte.
     */
    public int readByte() {
        return readByte(true, ValueType.STANDARD);
    }

    /**
     * Reads a standard byte.
     * 
     * @param signed
     *            if the byte is signed.
     * @return the value of the byte.
     */
    public int readByte(boolean signed) {
        return readByte(signed, ValueType.STANDARD);
    }

    /**
     * Reads a signed byte.
     * 
     * @param type
     *            the value type.
     * @return the value of the byte.
     */
    public int readByte(ValueType type) {
        return readByte(true, type);
    }

    /**
     * Reads a short value.
     * 
     * @param signed
     *            if the short is signed.
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return the value of the short.
     */
    public int readShort(boolean signed, ValueType type, ByteOrder order) {
        int value = 0;
        switch (order) {
        case BIG:
            value |= readByte(false) << 8;
            value |= readByte(false, type);
            break;
        case MIDDLE:
            throw new UnsupportedOperationException(
                "Middle-endian short is impossible!");
        case INVERSE_MIDDLE:
            throw new UnsupportedOperationException(
                "Inverse-middle-endian short is impossible!");
        case LITTLE:
            value |= readByte(false, type);
            value |= readByte(false) << 8;
            break;
        }
        return signed ? value : value & 0xffff;
    }

    /**
     * Reads a standard signed big-endian short.
     * 
     * @return the value of the short.
     */
    public int readShort() {
        return readShort(true, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a standard big-endian short.
     * 
     * @param signed
     *            if the short is signed.
     * @return the value of the short.
     */
    public int readShort(boolean signed) {
        return readShort(signed, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a signed big-endian short.
     * 
     * @param type
     *            the value type.
     * @return the value of the short.
     */
    public int readShort(ValueType type) {
        return readShort(true, type, ByteOrder.BIG);
    }

    /**
     * Reads a big-endian short.
     * 
     * @param signed
     *            if the short is signed.
     * @param type
     *            the value type.
     * @return the value of the short.
     */
    public int readShort(boolean signed, ValueType type) {
        return readShort(signed, type, ByteOrder.BIG);
    }

    /**
     * Reads a signed standard short.
     * 
     * @param order
     *            the byte order.
     * @return the value of the short.
     */
    public int readShort(ByteOrder order) {
        return readShort(true, ValueType.STANDARD, order);
    }

    /**
     * Reads a standard short.
     * 
     * @param signed
     *            if the short is signed.
     * @param order
     *            the byte order.
     * @return the value of the short.
     */
    public int readShort(boolean signed, ByteOrder order) {
        return readShort(signed, ValueType.STANDARD, order);
    }

    /**
     * Reads a signed short.
     * 
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return the value of the short.
     */
    public int readShort(ValueType type, ByteOrder order) {
        return readShort(true, type, order);
    }

    /**
     * Reads an integer.
     * 
     * @param signed
     *            if the short is signed.
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return the value of the integer.
     */
    public long readInt(boolean signed, ValueType type, ByteOrder order) {
        long value = 0;
        switch (order) {
        case BIG:
            value |= readByte(false) << 24;
            value |= readByte(false) << 16;
            value |= readByte(false) << 8;
            value |= readByte(false, type);
            break;
        case MIDDLE:
            value |= readByte(false) << 8;
            value |= readByte(false, type);
            value |= readByte(false) << 24;
            value |= readByte(false) << 16;
            break;
        case INVERSE_MIDDLE:
            value |= readByte(false) << 16;
            value |= readByte(false) << 24;
            value |= readByte(false, type);
            value |= readByte(false) << 8;
            break;
        case LITTLE:
            value |= readByte(false, type);
            value |= readByte(false) << 8;
            value |= readByte(false) << 16;
            value |= readByte(false) << 24;
            break;
        }
        return signed ? value : value & 0xffffffffL;
    }

    /**
     * Reads a signed standard big-endian integer.
     * 
     * @return the value of the integer.
     */
    public int readInt() {
        return (int) readInt(true, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a standard big-endian integer.
     * 
     * @param signed
     *            if the short is signed.
     * @return the value of the integer.
     */
    public long readInt(boolean signed) {
        return readInt(signed, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a signed big-endian integer.
     * 
     * @param type
     *            the value type.
     * @return the value of the integer.
     */
    public int readInt(ValueType type) {
        return (int) readInt(true, type, ByteOrder.BIG);
    }

    /**
     * Reads a big-endian integer.
     * 
     * @param signed
     *            if the short is signed.
     * @param type
     *            the value type.
     * @return the value of the integer.
     */
    public long readInt(boolean signed, ValueType type) {
        return readInt(signed, type, ByteOrder.BIG);
    }

    /**
     * Reads a signed standard integer.
     * 
     * @param order
     *            the byte order.
     * @return the value of the integer.
     */
    public int readInt(ByteOrder order) {
        return (int) readInt(true, ValueType.STANDARD, order);
    }

    /**
     * Reads a standard integer.
     * 
     * @param signed
     *            if the short is signed.
     * @param order
     *            the byte order.
     * @return the value of the integer.
     */
    public long readInt(boolean signed, ByteOrder order) {
        return readInt(signed, ValueType.STANDARD, order);
    }

    /**
     * Reads a signed integer.
     * 
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return the value of the integer.
     */
    public int readInt(ValueType type, ByteOrder order) {
        return (int) readInt(true, type, order);
    }

    /**
     * Reads a signed long value.
     * 
     * @param type
     *            the value type.
     * @param order
     *            the byte order.
     * @return the value of the integer.
     */
    public long readLong(ValueType type, ByteOrder order) {
        long value = 0;
        switch (order) {
        case BIG:
            value |= (long) readByte(false) << 56L;
            value |= (long) readByte(false) << 48L;
            value |= (long) readByte(false) << 40L;
            value |= (long) readByte(false) << 32L;
            value |= (long) readByte(false) << 24L;
            value |= (long) readByte(false) << 16L;
            value |= (long) readByte(false) << 8L;
            value |= readByte(false, type);
            break;
        case MIDDLE:
            throw new UnsupportedOperationException(
                "middle-endian long is not implemented!");
        case INVERSE_MIDDLE:
            throw new UnsupportedOperationException(
                "inverse-middle-endian long is not implemented!");
        case LITTLE:
            value |= readByte(false, type);
            value |= (long) readByte(false) << 8L;
            value |= (long) readByte(false) << 16L;
            value |= (long) readByte(false) << 24L;
            value |= (long) readByte(false) << 32L;
            value |= (long) readByte(false) << 40L;
            value |= (long) readByte(false) << 48L;
            value |= (long) readByte(false) << 56L;
            break;
        }
        return value;
    }

    /**
     * Reads a signed standard big-endian long.
     * 
     * @return the value of the long.
     */
    public long readLong() {
        return readLong(ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a signed big-endian long
     * 
     * @param type
     *            the value type
     * @return the value of the long.
     */
    public long readLong(ValueType type) {
        return readLong(type, ByteOrder.BIG);
    }

    /**
     * Reads a signed standard long.
     * 
     * @param order
     *            the byte order
     * @return the value of the long.
     */
    public long readLong(ByteOrder order) {
        return readLong(ValueType.STANDARD, order);
    }

    /**
     * Reads a RuneScape string value.
     * 
     * @return the value of the string.
     */
    public String readString() {
        byte temp;
        StringBuilder b = new StringBuilder();
        while ((temp = (byte) readByte()) != 10) {
            b.append((char) temp);
        }
        return b.toString();
    }

    /**
     * Reads the amount of bytes into the array, starting at the current
     * position.
     * 
     * @param amount
     *            the amount to read.
     * @return a buffer filled with the data.
     */
    public byte[] readBytes(int amount) {
        return readBytes(amount, ValueType.STANDARD);
    }

    /**
     * Reads the amount of bytes into a byte array, starting at the current
     * position.
     * 
     * @param amount
     *            the amount of bytes.
     * @param type
     *            the value type of each byte.
     * @return a buffer filled with the data.
     */
    public byte[] readBytes(int amount, ValueType type) {
        byte[] data = new byte[amount];
        for (int i = 0; i < amount; i++) {
            data[i] = (byte) readByte(type);
        }
        return data;
    }

    /**
     * Reads the amount of bytes from the buffer in reverse, starting at current
     * position + amount and reading in reverse until the current position.
     * 
     * @param amount
     *            the amount of bytes to read.
     * @param type
     *            the value type of each byte.
     * @return a buffer filled with the data.
     */
    public byte[] readBytesReverse(int amount, ValueType type) {
        byte[] data = new byte[amount];
        int dataPosition = 0;
        for (int i = buf.position() + amount - 1; i >= buf.position(); i--) {
            int value = buf.get(i);
            switch (type) {
            case A:
                value -= 128;
                break;
            case C:
                value = -value;
                break;
            case S:
                value = 128 - value;
                break;
            case STANDARD:
                break;
            }
            data[dataPosition++] = (byte) value;
        }
        return data;
    }

    /**
     * Gets the backing byte buffer.
     * 
     * @return the backing byte buffer.
     */
    public ByteBuffer getBuffer() {
        return buf;
    }
}
