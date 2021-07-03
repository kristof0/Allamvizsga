package model.Loaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

public class StandardLoader extends LoaderTemplate implements Serializable {
    public StandardLoader(String savePath) {
        super(savePath);
    }

    @Override
    public float[] getTextCoordinate(int nthImage) {
        float t=nthImage%numbeOfImagesPerTexture;
        float[] textCoords=new float[]{
                1.0f/(numbeOfImagesPerTexture)*t,0.0f,
                1.0f/(numbeOfImagesPerTexture)*t,1.0f,
                1.0f/(numbeOfImagesPerTexture)*(t+1),1.0f,
                1.0f/(numbeOfImagesPerTexture)*(t+1),0.0f,

        };
        return textCoords;
    }

    @Override
    public void setTextCoordinate(float[] textCoords, int nthPos, int nthPic) {
        int t=nthPos%numbeOfImagesPerTexture;
        int k=(nthPic%numbeOfImagesPerTexture)*8;
        textCoords[k]= 1.0f/(numbeOfImagesPerTexture)*t;
        textCoords[k+1]=0.0f;
        textCoords[k+2]=1.0f/(numbeOfImagesPerTexture)*t;
        textCoords[k+3]=1.0f;
        textCoords[k+4]=1.0f/(numbeOfImagesPerTexture)*(t+1);
        textCoords[k+5]=1.0f;
        textCoords[k+6]=1.0f/(numbeOfImagesPerTexture)*(t+1);
        textCoords[k+7]=0.0f;
    }



    @Override
    protected void addImage(BufferedImage newImage) throws IOException {
        if(xImage==maxWidth){
            saveTexture();
            // imageList.addLast(image);
        }
        g.drawImage(newImage, xImage, 0, null);
        xImage += size;
        ++numberOfImages;
    }
}
