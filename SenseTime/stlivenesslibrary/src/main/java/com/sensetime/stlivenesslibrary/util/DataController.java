package com.sensetime.stlivenesslibrary.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.TextureView;

import com.sensetime.stlivenesslibrary.LivenessDetector.Motion;
/**
 *
 * @author Tracy
 *
 */
public class DataController {
	public static final String SDCARD_STORAGE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/liveness/";
	public static final int BUFFER = 1024;

	public static void writeFileToSD(String folderName, String fileName,
			String s) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		try {
			File path = new File(folderName);
			File file = new File(folderName + fileName);
			if (!path.exists()) {
				path.mkdir();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file);
			// String s = "this is a test string writing to file.";
			byte[] buf = s.getBytes();
			stream.write(buf);
			stream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeToFile(String fileName, String input) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					fileName), true));
			writer.write(input + "\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createSDCardDir(String folderName) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/" + folderName;
			File path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
		} else {
			return;
		}
	}

	public static Bitmap convertNv21ToBmp(byte[] data) throws IOException {
		YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21,
				Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		yuvImage.compressToJpeg(new Rect(0, 0, Constants.PREVIEW_WIDTH,
				Constants.PREVIEW_HEIGHT), 100, baos);
		byte[] jpegBytes = baos.toByteArray();
		baos.flush();
		baos.close();
		Bitmap photoBmp = BitmapFactory.decodeByteArray(jpegBytes, 0,
				jpegBytes.length);
		Matrix matrix = new Matrix();
		matrix.postRotate(-90);
		Bitmap cropBitmap = null;
		if (photoBmp != null) {
			cropBitmap = Bitmap.createBitmap(photoBmp, 0, 0,
					Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT, matrix,
					false);
		}
		return cropBitmap;
	}

	public static void saveImageFile(String sdcardPath, Bitmap bm,
			String fileName) throws IOException {
		File dirFile = new File(sdcardPath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(sdcardPath + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 90, bos);
		bos.flush();
		bos.close();
	}

	public static void saveToSDCard(String folderName, String fileName,
			ByteArrayOutputStream baos) {
		File file = new File(folderName);
		if (!file.exists()) {
			file.mkdir();
		}
		if (file.exists()) {
			File f = new File(folderName, fileName + ".jpg");
			if (f.exists()) {
				f.delete();
			}
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					FileOutputStream out = new FileOutputStream(f);
					baos.writeTo(out);
					out.flush();
					out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDetectPicturePath(String folderName) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/" + folderName;
			File path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
		} else {
			return;
		}
	}

	/**
	 * zip compress. 压缩baseDir(文件夹目录)下所有文件，包括子目录
	 *
	 * @throws Exception
	 */
	public static void zipFile(String baseDir, String fileName)
			throws Exception {
		List fileList = getSubFiles(new File(baseDir));
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(baseDir
				+ fileName));
		ZipEntry ze = null;
		byte[] buf = new byte[BUFFER];
		int readLen = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			ze = new ZipEntry(getAbsFileName(baseDir, f));
			ze.setSize(f.length());
			ze.setTime(f.lastModified());
			zos.putNextEntry(ze);
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			while ((readLen = is.read(buf, 0, BUFFER)) != -1) {
				zos.write(buf, 0, readLen);
			}
			is.close();
		}
		zos.close();
	}

	/**
	 * 取得指定目录下的所有文件列表，包括子目录.
	 *
	 * @param baseDir
	 *            File 指定的目录
	 * @return 包含java.io.File的List
	 */
	private static List getSubFiles(File baseDir) {
		List ret = new ArrayList();
		File[] tmp = baseDir.listFiles();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].isFile())
				ret.add(tmp[i]);
			// if (tmp[i].isDirectory())
			// ret.addAll(getSubFiles(tmp[i]));
		}
		return ret;
	}

	/**
	 * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.
	 *
	 * @param baseDir
	 *            java.lang.String 根目录
	 * @param realFileName
	 *            java.io.File 实际的文件名
	 * @return 相对文件名
	 */
	private static String getAbsFileName(String baseDir, File realFileName) {
		File real = realFileName;
		File base = new File(baseDir);
		String ret = real.getName();
		while (true) {
			real = real.getParentFile();
			if (real == null)
				break;
			if (real.equals(base))
				break;
			else
				ret = real.getName() + "/" + ret;
		}
		return ret;
	}

	public static String getUuidFromJson(String json) {
		String uuid = "";
		try {
			JSONObject jsonObject = new JSONObject(json);
			uuid = jsonObject.getString(Constants.UUID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uuid;
	}

	public static String getUseridFromJson(String json) {
		String userid = "";
		try {
			JSONObject jsonObject = new JSONObject(json);
			userid = jsonObject.getString(Constants.USERID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userid;
	}

	public static List<String> getSequencedFromJson(String json) {
		List<String> list = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = new JSONArray();
			jsonArray = jsonObject.getJSONArray(Constants.SEQUENCE);
			list = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(new String((String) jsonArray.get(i)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static String getOuttypeFromJson(String json) {
		String outType = "";
		try {
			JSONObject jsonObject = new JSONObject(json);
			outType = jsonObject.getString(Constants.OUTTYPE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outType;
	}

	public static Motion[] setDetectActionOrder(String input) {
		String[] splitStrings = input.split("\\s+");
		Motion[] detectList = new Motion[splitStrings.length];
		for (int i = 0; i < splitStrings.length; i++) {
			if (splitStrings[i].equalsIgnoreCase(Constants.BLINK)) {
				detectList[i] = Motion.BLINK;
			} else if (splitStrings[i].equalsIgnoreCase(Constants.NOD)) {
				detectList[i] = Motion.NOD;
			} else if (splitStrings[i].equalsIgnoreCase(Constants.MOUTH)) {
				detectList[i] = Motion.MOUTH;
			} else if (splitStrings[i].equalsIgnoreCase(Constants.YAW)) {
				detectList[i] = Motion.YAW;
			}
		}
		return detectList;
	}

	public static String[] getDetectActionOrder(String input) {
		String[] splitStrings = input.split("\\s+");
		return splitStrings;
	}

	public static float[] getThreshold(String input) {
		String[] splitStrings = input.split("\\s+");
		float[] mThreshold = new float[splitStrings.length];
		for (int i = 0; i < splitStrings.length; i++) {
			mThreshold[i] = Float.parseFloat(splitStrings[i]);
		}
		return mThreshold;
	}

	public static boolean[] getLost(String input) {
		String[] splitStrings = input.split("\\s+");
		boolean [] mLost = new boolean[splitStrings.length];
		for(int i=0;i<splitStrings.length;i++) {
			if(splitStrings[i].equalsIgnoreCase("true")) {
				mLost[i] = true;
			} else if(splitStrings[i].equalsIgnoreCase("false")){
				mLost[i] = false;
			}
		}
		return mLost;
	}

	public static HashMap<String, String> getResultFromJson(String json) {
		HashMap<String, String> map = new HashMap<String, String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
			if (jsonObject.has(Constants.ERROR)) {
				map.put(Constants.ERROR, jsonObject.getString(Constants.ERROR));
				map.put(Constants.REQUEST_ID,
						jsonObject.getString(Constants.REQUEST_ID));
				map.put(Constants.TIME_USED,
						jsonObject.getString(Constants.TIME_USED));
				map.put(Constants.ALIVE, "false");
			} else {
				map.put(Constants.REQUEST_ID,
						jsonObject.getString(Constants.REQUEST_ID));
				map.put(Constants.TIME_USED,
						jsonObject.getString(Constants.TIME_USED));
				JSONObject faceidObject = new JSONObject();
				faceidObject = jsonObject
						.getJSONObject(Constants.RESULT_FACEID);
				if (faceidObject.has(Constants.ALIVE)) {
					map.put(Constants.ALIVE,
							faceidObject.getString(Constants.ALIVE));
				} else {
					map.put(Constants.ALIVE, "false");
				}
				map.put(Constants.CONFIDENCE,
						faceidObject.getString(Constants.CONFIDENCE));
				JSONObject thresholdsObject = faceidObject
						.getJSONObject(Constants.THRESHOLDS);
				map.put(Constants.ONEE5,
						thresholdsObject.getString(Constants.ONEE5));
				map.put(Constants.ONEE4,
						thresholdsObject.getString(Constants.ONEE4));
				map.put(Constants.ONEE3,
						thresholdsObject.getString(Constants.ONEE3));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * Get the device id
	 *
	 * @param context
	 *
	 * @return
	 */
	public static String getLocalDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		if (deviceId == null || deviceId.trim().length() == 0) {
			deviceId = String.valueOf(System.currentTimeMillis());
		}
		return deviceId;
	}

	public static void deleteFiles(String folderPath) {
	    if (TextUtils.isEmpty(folderPath)) {
	        return;
	    }
		File dir = new File(folderPath);
		if (dir == null || !dir.exists() || !dir.isDirectory() || dir.listFiles() == null)
			return;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete();
		}
	}

}
