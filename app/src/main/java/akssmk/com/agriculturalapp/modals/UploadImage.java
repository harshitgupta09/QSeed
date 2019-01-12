package akssmk.com.agriculturalapp.modals;

public class UploadImage {
    private String mImageUrl;


    public UploadImage() {
        //empty constructor needed
    }

    public UploadImage(String imageUrl) {
        this.mImageUrl = imageUrl;

    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
