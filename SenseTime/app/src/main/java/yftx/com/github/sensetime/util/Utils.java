package yftx.com.github.sensetime.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

/**
 * Created by tracy on 4/1/16.
 */
public class Utils {
	public static String storageFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ "liveness" + File.separator;
	private static Toast toast;

	public static void ToastUtil(Context context, String content, String timeStamp) {
		if(toast == null) {
			toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
		} else {
			toast.setText(content);
		}
		
		toast.show();
	}
	
	public static int getImageCountInPath(String path) {
		int i = 0;
		File file = new File(path);
		File[] files = file.listFiles();
		for (int j = 0; j < files.length; j++) {
			String name = files[j].getName();
			if (files[j].isDirectory()) {
				String dirPath = files[j].toString().toLowerCase();
				getImageCountInPath(dirPath + "/");
			} else if (files[j].isFile() & name.endsWith(".jpg")) {
				i++;
			}
		}
		return i;
	}

	public static List<String> getImageListName(String path) {
		List<String> imageList = new ArrayList<String>();
		File file = new File(path);
		File[] files = file.listFiles();
		for (int j = 0; j < files.length; j++) {
			if (files[j].isFile() & files[j].getName().endsWith(".jpg")) {
				imageList.add(files[j].getName());
			}
		}
		return imageList;
	}

	public String[] getDetectStringInArray(String input) {
		String[] splitStrings = input.split("\\s+");
		return splitStrings;
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 *            the file url
	 * @return Bitmap
	 */
	public static Bitmap getLoacalBitmap(String url) {
		Bitmap bitmap = null;
		try {
			FileInputStream fis = new FileInputStream(url);
			bitmap = BitmapFactory.decodeStream(fis);
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
