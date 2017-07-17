package br.com.wasys.gfin.cheqfast.cliente.service;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import br.com.wasys.gfin.cheqfast.cliente.BuildConfig;
import br.com.wasys.gfin.cheqfast.cliente.Permission;
import br.com.wasys.library.service.Service;
import br.com.wasys.library.utils.AndroidUtils;
import br.com.wasys.library.utils.DateUtils;
import br.com.wasys.library.utils.FileUtils;

/**
 * Created by pascke on 03/09/16.
 */
public class ImagemService extends Service {

    private static final String TAG = "Imagem";

    /**
     *
     * @return
     * @throws IllegalStateException
     */
    private static File getAppDirectory() throws IllegalStateException {
        AndroidUtils.throwIfExternalStorageNotWritable();
        File storageDirectory = Environment.getExternalStorageDirectory();
        File directory = new File(storageDirectory, BuildConfig.APP_DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    /**
     *
     * @return
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    public static Uri createUploadUri() throws FileNotFoundException, SecurityException, IllegalStateException {
        File file = createArbitrary("upload", false);
        Uri uri =  Uri.fromFile(file);
        return uri;
    }

    /**
     *
     * @param deleteOnExit
     * @return
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    public static Uri createTmpUri(boolean deleteOnExit) throws FileNotFoundException, SecurityException, IllegalStateException {
        File file = createArbitrary("tmp", deleteOnExit);
        Uri uri =  Uri.fromFile(file);
        return uri;
    }

    /**
     *
     * @param path
     * @param deleteOnExit
     * @return
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    public static Uri createViewUri(String path, boolean deleteOnExit) throws FileNotFoundException, SecurityException, IllegalStateException {
        Uri uri = createAndCopy(path, "view", deleteOnExit);
        return uri;
    }

    /**
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    public static Uri createUploadUri(String path) throws FileNotFoundException, SecurityException, IllegalStateException {
        Uri uri = createAndCopy(path, "upload", false);
        return uri;
    }

    /**
     *
     * @param path
     * @param folder
     * @param deleteOnExit
     * @return
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    private static Uri createAndCopy(String path, String folder, boolean deleteOnExit) throws FileNotFoundException, SecurityException, IllegalStateException {
        File origin = new File(path);
        if (!origin.exists()) {
            throw new FileNotFoundException("Not found '" + path + "'.");
        }
        Permission.throwIfStorageNotGranted();
        AndroidUtils.throwIfExternalStorageNotWritable();
        try {
            File destination = createArbitrary(folder, true);
            if (deleteOnExit) {
                destination.deleteOnExit();
            }
            FileUtils.copy(origin, destination);
            Uri uri =  Uri.fromFile(destination);
            return uri;
        } catch (IOException e) {
            String message = "Fail to create image in " + folder + ".";
            Log.e(TAG, message, e);
            throw new IllegalStateException(message);
        }
    }

    /**
     *
     * @param folder
     * @param deleteOnExit
     * @return
     * @throws FileNotFoundException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    private static File createArbitrary(String folder, boolean deleteOnExit) throws FileNotFoundException, SecurityException, IllegalStateException {
        Permission.throwIfStorageNotGranted();
        AndroidUtils.throwIfExternalStorageNotWritable();
        File directory = getAppDirectory();
        try {
            File root = new File(directory.getAbsolutePath(), folder);
            if (!root.exists()) {
                root.mkdirs();
            }
            Date now = new Date();
            String name = DateUtils.format(now, "yyyyMMdd_HHmmssSSS.'jpg'");
            File file = new File(root, name);
            file.createNewFile();
            /*if (deleteOnExit) {
                file.deleteOnExit();
            }*/
            return file;
        } catch (IOException e) {
            String message = "Fail to create image in " + folder + ".";
            Log.e(TAG, message, e);
            throw new IllegalStateException(message);
        }
    }
}