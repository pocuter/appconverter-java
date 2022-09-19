package com.pocuter.converter;

import java.io.File;
import java.util.Locale;

public class Main {
	public static void main(String args[]) {
//		if (args.length == 0)
//			args = "-image ScoreTracker.ino.esp32c3.bin -id 4 -version 0.2.2".split(" ");
		
		long id = 0;
		int vMajor = 0, vMinor = 0, vPatch = 0;
		String binaryPath = null, metaPath = null;
		boolean printHelp = false;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-id")) {
				if (i+1 < args.length) {
					id = Long.parseLong(args[i+1], 10);
					i += 1;
				}
				continue;
			}
			if (args[i].equals("-version")) {
				if (i+1 < args.length) {
					String[] version = args[i+1].split("\\.");
					try {
						vMajor = Integer.parseInt(version[0], 10);
						vMinor = Integer.parseInt(version[1], 10);
						vPatch = Integer.parseInt(version[2], 10);
					} catch (ArrayIndexOutOfBoundsException | NumberFormatException exc) {
						System.err.println("Invalid format for version: must be [X.Y.Z], values from 0-255 only");
						return;
					}
					i += 1;
				}
				continue;
			}
			if (args[i].equals("-meta")) {
				if (i+1 < args.length) {
					metaPath = args[i+1];
					i += 1;
				}
				continue;
			}
			if (args[i].equals("-image")) {
				if (i+1 < args.length) {
					binaryPath = args[i+1];
					i += 1;
				}
				continue;
			}
			if (args[i].equals("-help")) {
				printHelp = true;
				continue;
			}
		}

		File binaryFile = binaryPath == null ? null : new File(binaryPath);
		File metaFile = metaPath == null ? null : new File(metaPath);
		
		// check if files exist
		
		if (binaryFile != null && (!binaryFile.exists() || !binaryFile.isFile())) {
			System.err.println("Binary file not found.");
			binaryFile = null;
		}
		
		if (metaFile != null && (!metaFile.exists() || !metaFile.isFile())) {
			System.err.println("Meta file not found; creating app without metadata.");
			metaFile = null;
		}
		
		// check for valid args
		
		if (binaryFile == null) {
			System.err.println("Cannot create app without binary file.");
			return;
		}
		
		if (id <= 0) {
			System.err.println("Invalid ID: must be greater than 0.");
			return;
		}
		
		if ((vMajor < 0 || vMajor > 255) || (vMinor < 0 || vMinor > 255) || (vPatch < 0 || vPatch > 255)) {
			System.err.println("Invalid version: values must be between 0 and 255.");
			return;
		}
		
		if (printHelp) {
			printHelp();
			return;
		}

		System.out.println(String.format(Locale.ROOT, "Using image \"%s\"", binaryPath));
		System.out.println(String.format(Locale.ROOT, "Using ID %d", id));
		System.out.println(String.format(Locale.ROOT, "Using version %d.%d.%d", vMajor, vMinor, vPatch));
		if (metaFile != null)
			System.out.println(String.format(Locale.ROOT, "Using meta file \"%s\"", metaPath));
		
		String path = "apps/" + id + "/esp32c3.app";
		File outFile = new File(path);
		outFile.getParentFile().mkdirs();
		AppConverter.convert(outFile, binaryFile, metaFile, null, id, vMajor, vMinor, vPatch);
		System.out.println("OK, file saved to " + path);
	}
	
	private static void printHelp() {
	    System.out.print("Usage:\n\n");
	    System.out.print("   -image   [image filename]\n");
	    System.out.print("   -version [X.Y.Z]\n");
	    System.out.print("   -meta    [metadata filename]\n");
//	    System.out.print("   -sign    [private key filename]\n");
	    System.out.print("   -id      [APP-ID]\n");
	    System.out.print("   -help    \n");
	}
}
