package com.pocuter.converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Deflater;

public class ZlibPocuter {
	public static byte[] compress(byte input[]) throws IOException {
		byte[] output = new byte[input.length];
		int outputOffset = 0;
		int inputOffset = 0;
		
		while (true) {
			int blockSize = Math.min(1024*40, input.length-inputOffset);
			byte[] buffer = Arrays.copyOfRange(input, inputOffset, inputOffset+blockSize);
			
			Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
			deflater.setInput(buffer);
			deflater.finish();
			
			int blockSizeCompressed = deflater.deflate(output, outputOffset+4, input.length-outputOffset-4);
			insertIntIntoByteArray(output, outputOffset, blockSizeCompressed);
			
			inputOffset += blockSize;
			outputOffset += (4 + blockSizeCompressed);
			
			if (inputOffset >= input.length)
				break;
		}
		
		return Arrays.copyOf(output, outputOffset);
	}
	
	private static void insertIntIntoByteArray(byte[] array, int offset, int data) {
		array[offset  ] = (byte) ((data       ) & 0xFF);
		array[offset+1] = (byte) ((data >>>  8) & 0xFF);
		array[offset+2] = (byte) ((data >>> 16) & 0xFF);
		array[offset+3] = (byte) ((data >>> 24) & 0xFF);
	}
}
