package charlie.flickrrocket;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by charlie on 2/11/15.
 */
public class ImageModel {

    private static ImageModel _instance;
    private ArrayList<ImageData> imageList;
    private int index;
    private Bitmap currentBitmap;
    private String lastSearchTag;
    private String lastImageDescription;

    //  Private constructor, only called once
    private ImageModel() {
        imageList = new ArrayList<>();
        index = 0;
        currentBitmap = null;
        lastSearchTag = "rocket";
        lastImageDescription = "";
    }

    //  Public function to get singleton instance of ImageModel
    //  Lazily instantiates the ImageModel
    public static ImageModel getInstance() {
        if (_instance == null)
            _instance = new ImageModel();
        return _instance;
    }

    //  Adds an object of type ImageData to the ArrayList
    public void add(ImageData imageData) {
        imageList.add(imageData);
    }

    //  Retrieves the next, same, or previous image in the array based on the value of indexChange
    //  if index is out of bounds of the array, the index is wrapped around to the other end of the
    //  array
    public ImageData getNextImage(int indexChange) {
        index += indexChange;
        if (index >= imageList.size())
            index = 0;
        if (index < 0)
            index = imageList.size() - 1;
        return imageList.get(index);
    }

    //  Returns the size of the imageArray
    public int size() {
        return imageList.size();
    }

    //  Returns whether or not the imageArray is empty
    public boolean isEmpty() {
        return imageList.isEmpty();
    }

    //  Clears the imageArray, resets the current index, and clears the current Bitmap
    public void clear() {
        imageList.clear();
        index = 0;
        currentBitmap = null;
    }


    public Bitmap getCurrentBitMap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap bmp) {
        this.currentBitmap = bmp;
    }

    //  Checks if the ImageModel has a stored Bitmap
    public boolean hasBitmap() {
        if (currentBitmap == null)
            return false;
        else
            return true;
    }

    public String getLastSearchTag() {
        return lastSearchTag;
    }

    public void setLastSearchTag(String tag) {
        this.lastSearchTag = tag;
    }

    public String getLastImageDescription() {
        return lastImageDescription;
    }

    public void setLastImageDescription(String description) {
        this.lastImageDescription = description;
    }
}
