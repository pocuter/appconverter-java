package com.pocuter.converter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppConverter {
	private static FileOutputStream fos;
	
	public static void convert(File outFile, File binaryFile, File metaFile, File signatureFile, long id, int vMajor, int vMinor, int vPatch) {
		try {
			byte metaBytes[] = new byte[0], signatureBytes[] = new byte[0], binaryBytes[] = new byte[0];
			
			DataInputStream disb = new DataInputStream(new FileInputStream(binaryFile));
			binaryBytes = new byte[(int) binaryFile.length()];
			disb.read(binaryBytes);
			disb.close();
			binaryBytes = ZlibPocuter.compress(binaryBytes);
		
			if (metaFile != null) {
				DataInputStream dism = new DataInputStream(new FileInputStream(metaFile));
				metaBytes = new byte[(int) metaFile.length() + 3];
				dism.read(metaBytes);
				dism.close();
				metaBytes[metaBytes.length-3] = (byte) ('\n' & 0xFF);
				metaBytes[metaBytes.length-2] = (byte) ('\0' & 0xFF);
				metaBytes[metaBytes.length-1] = (byte) (-1 & 0xFF);
			}

			if (signatureFile != null) {
				DataInputStream diss = new DataInputStream(new FileInputStream(signatureFile));
				signatureBytes = new byte[(int) signatureFile.length()];
				diss.read(signatureBytes);
				diss.close();
			}
			
			fos = new FileOutputStream(outFile);
			
			// signature: 5x uint8
			fosWriteByte((byte) ('P' & 0xFF));
			fosWriteByte((byte) ('1' & 0xFF));
			fosWriteByte((byte) ('A' & 0xFF));
			fosWriteByte((byte) ('P' & 0xFF));
			fosWriteByte((byte) ('P' & 0xFF));
			
			// file version: 1x uint8
			fosWriteByte((byte) (0 & 0xFF));
			
			// CRC 16: 1x uint16
			fosWriteShort((short) 0);
			
			// app ID: 1x uint64
			fosWriteLong(id);
			
			// version: 3x uint8
			fosWriteByte((byte) (vMajor & 0xFF));
			fosWriteByte((byte) (vMinor & 0xFF));
			fosWriteByte((byte) (vPatch & 0xFF));
			
			// features: 1x uint8 bitfield
			byte features = 0;
			if (signatureFile != null) features |= (1 << 0);
			if (metaFile      != null) features |= (1 << 1);
			fosWriteByte(features);
			
			//               sig fvr crc id  ver fts sof ssz mof msz bof bsz
			int HEADER_SIZE = 5 + 1 + 2 + 8 + 3 + 1 + 4 + 4 + 4 + 4 + 4 + 4;
			
			// signature start & size: 2x uint32
			fosWriteInt(0);
			fosWriteInt(0);
			
			// meta start & size: 2x uint32
			if (metaFile == null) {
				fosWriteInt(0);
				fosWriteInt(0);
			} else {
				fosWriteInt(HEADER_SIZE);
				fosWriteInt(metaBytes.length);
			}
			
			// flash start & size: 2x uint32
			fosWriteInt(HEADER_SIZE + metaBytes.length);
			fosWriteInt(binaryBytes.length);
			
			// write meta
			fosWriteByteArray(metaBytes);
			
			// write flash
			fosWriteByteArray(binaryBytes);
			
			fos.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		fos = null;
	}
	
	private static void fosWriteByte(byte b) throws IOException {
		fos.write(new byte[] {b});
	}
	
	private static void fosWriteByteArray(byte b[]) throws IOException {
		fos.write(b);
	}
	
	private static void fosWriteShort(short s) throws IOException {
		fos.write(new byte[] {
				(byte) ((s      ) & 0xFF),
				(byte) ((s >>> 8) & 0xFF),
			});
	}
	
//	private static void fosWriteShortArray(short s[]) throws IOException {
//		for (short t : s)
//			fosWriteShort(t);
//	}
	
	private static void fosWriteInt(int i) throws IOException {
		fos.write(new byte[] {
				(byte) ((i       ) & 0xFF),
				(byte) ((i >>>  8) & 0xFF),
				(byte) ((i >>> 16) & 0xFF),
				(byte) ((i >>> 24) & 0xFF),
			});
	}
	
//	private static void fosWriteIntArray(int i[]) throws IOException {
//		for (int j : i)
//			fosWriteInt(j);
//	}
	
	private static void fosWriteLong(long l) throws IOException {
		fos.write(new byte[] {
				(byte) ((l       ) & 0xFF),
				(byte) ((l >>>  8) & 0xFF),
				(byte) ((l >>> 16) & 0xFF),
				(byte) ((l >>> 24) & 0xFF),
				(byte) ((l >>> 32) & 0xFF),
				(byte) ((l >>> 40) & 0xFF),
				(byte) ((l >>> 48) & 0xFF),
				(byte) ((l >>> 56) & 0xFF),
			});
	}
}
