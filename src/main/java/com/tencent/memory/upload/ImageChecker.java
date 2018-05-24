package com.tencent.memory.upload;

/**
 * ImageChecker.
 * 
 * @author michalewang
 * @version 2015-03-18
 */
public class ImageChecker {

	/**
	 * JPEG文件头标识 (2 bytes): 0xff, 0xd8 (SOI) JPEG文件尾标识 (2 bytes): 0xff, 0xd9
	 * (EOI)
	 */
	public static int JPG[] = { 0xFF, 0xD8 };

	/**
	 * BMP文件头标识 (2 bytes): 42 4D
	 */
	public static int BMP[] = { 0x42, 0x4D };

	/**
	 * PNG文件头标识 (8 bytes) 89 50 4E 47 0D 0A 1A 0A， 其中2 ~ 4 字节，分别是PNG
	 */
	public static int PNG[] = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

	/**
	 * GIF文件头标识 (6 bytes) 47 49 46 38 39(37) 61， 其中1 ~ 3 字节，分别是GIF
	 */
	public static int GIF_39[] = { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61 };
	public static int GIF_37[] = { 0x47, 0x49, 0x46, 0x38, 0x37, 0x61 };

	/**
	 * WEBP文件头标识 (12 bytes) 1 ~ 4 字节，分别是 RIFF，9 ~ 12 字节，分别是WEBP 
	 * { 0x52, 0x49, 0x46, 0x46, 0x57, 0x45, 0x42, 0x50}
	 */
	public static int WEBP[] = { 0x52, 0x49, 0x46, 0x46, 0x57, 0x45, 0x42, 0x50};

	/**
	 * 判断是否为JPG格式图片.
	 * 
	 * @param data
	 * @return boolean
	 */
	public static boolean isJpeg(byte[] data) {
		return (ImageType.JPG == getImageType(data));
	}

	/**
	 * 根据文件头判断图片格式.
	 * 
	 * @param data
	 * @return ImageType
	 */
	public static ImageType getImageType(byte[] data) {
		ImageType type = ImageType.OTHERS;
		int first = getInt(data, 0);
		int second = getInt(data, 1);

		switch (first) {
		case 0xFF:
			if (second == JPG[1]) {
				type = ImageType.JPG;
				break;
			}

		case 0x42:
			if (second == BMP[1]) {
				type = ImageType.BMP;
				break;
			}

		case 0x47:
			if (startWith(data, GIF_37) || startWith(data, GIF_39)) {
				type = ImageType.GIF;
				break;
			}

		case 0x89:
			if (startWith(data, PNG)) {
				type = ImageType.PNG;
				break;
			}

		case 0x52:
			if (isWebP(data)) {
				type = ImageType.WEBP;
				break;
			}

		default:
			break;
		}

		return type;
	}

	/**
	 * 判断是否以指定的字节数组开头.
	 * 
	 * @param data
	 * @param image
	 * @return boolean
	 */
	public static boolean startWith(byte[] data, int image[]) {
		if (data.length < image.length) {
			return false;
		}

		for (int i = 0; i < image.length; i++) {
			if (getInt(data, i) != image[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 判断是否为WEBP格式图片.
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isWebP(byte[] data) {
		return (data.length > 12 && data[0] == 'R' && data[1] == 'I'
				&& data[2] == 'F' && data[3] == 'F' && data[8] == 'W'
				&& data[9] == 'E' && data[10] == 'B' && data[11] == 'P');
	}

	/**
	 * byte转换为int.
	 * 
	 * @param data
	 * @param index
	 * @return
	 */
	public static int getInt(byte[] data, int index) {
		return data[index] & 0xFF;
	}

}
