package customFeatures;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemDirectory;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.LireFeature;
import net.semanticmetadata.lire.utils.SerializationUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public final class LastModificationDate {




    public LastModificationDate(){}




    public String getFeatureName() {
        return "LastModificationDate";
    }

    /**
     *
     * @param n Image path
     * @return last modification date from metadatas
     * @throws ImageProcessingException
     * @throws IOException
     * @throws ParseException
     */
    public static Date GetDateMetadataFromPath(String n) throws ImageProcessingException, IOException, ParseException {
        File jpegFile = new File(String.valueOf(n));
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

        Directory directory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);

        //directory.getTagName(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
        String tag=directory.getDescription(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);

        SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd HH:mm:ss XXX yyyy", Locale.getDefault());
        Date date=sdf.parse(tag);

        return date;
    }

    public static Date ConvertStringToDate(String stringDate,String format) throws ParseException {

        SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.getDefault());//"EEE MMM dd HH:mm:ss yyyy"

        SimpleDateFormat dateFormat=new SimpleDateFormat(format);//"EEE MMM dd HH:mm yyyy"
        Date date=sdf.parse(stringDate);

        String formattedStringDate=dateFormat.format(date);
        SimpleDateFormat newFormatDate=new SimpleDateFormat(format,Locale.getDefault());

        return newFormatDate.parse(formattedStringDate);
    }

    public String getFieldName() {
        return "LastModificationDate";
    }


    }





