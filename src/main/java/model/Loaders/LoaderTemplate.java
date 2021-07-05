package model.Loaders;


import DB.MongoApi;

import ai.djl.Application;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.MalformedModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import controller.Messages.*;
import customFeatures.ObjectDetection;
import model.imgscalr.Scalr;
import model.jsquish.Squish;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;

import net.semanticmetadata.lire.imageanalysis.features.global.FuzzyOpponentHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.SimpleColorHistogram;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;

import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static customFeatures.LastModificationDate.*;
import static java.lang.Integer.parseInt;
import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static model.jsquish.Squish.CompressionType.DXT1;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glCompressedTexSubImage3D;
import static org.lwjgl.opengl.GL30.GL_MAX_ARRAY_TEXTURE_LAYERS;
import static org.lwjgl.opengles.GLES30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.system.MemoryUtil.NULL;


import view.engine.Utils;
import view.engine.tSNE;
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.FieldAccessor_Short;

public abstract class LoaderTemplate extends Observable implements Serializable,ILoader{
    protected static int MAX_SIZE;
    protected int size=0;
    protected static int openGLLimit;
    protected final int maxIconResolution=75;
    protected  int numbeOfImagesPerTexture;
    protected  int textureId=-1;
    protected  AtomicInteger numberOfTextures= new AtomicInteger(0);
    protected  int numberOfImages=0;
    transient protected LinkedList<BufferedImage> imageList;
    protected String savePath;
    protected HashMap<String,Date> metadataDates;
    protected ArrayList<Date> lastModifiedDates;

    protected LinkedList<Integer> groupsByDate;



    //object detection
    static boolean objectDetectionSwitch=false;
    transient protected  Criteria<BufferedImage, DetectedObjects> criteria;
    transient protected ZooModel<BufferedImage, DetectedObjects> zooModel = null;
    transient protected Predictor<BufferedImage, DetectedObjects> predictor=null;

    transient protected MongoApi DB=null;
    transient protected double[][] distanceBasedPositions=null;

    transient private int[] orderByTime;

    protected int maxRowSize=0;
    protected int maxColSize=0;


    protected int xImage=0;
    protected int maxWidth;
    transient protected BufferedImage image;
    transient protected Graphics g;

    public int getMaxNumberOfRows(){
        return maxRowSize;
    }
    public int getNumberOfCols(){
        return maxColSize;
    }
    public LinkedList<Integer> getGroupsByDate()
    { return groupsByDate;}

    protected LinkedList<Class> featuresList;
    //
    public LoaderTemplate(String savePath)
    {
        imageList=new LinkedList<BufferedImage>();
        this.savePath=savePath;
    }


    public int getNumberOfImagesPerTexture() {
        return numbeOfImagesPerTexture;
    }

    public int getNumberOfTextures() {
        return numberOfTextures.get();
    }

    public  int getNumberOfImages() {
        return numberOfImages;
    }

    public  int getTextureId(){
        return textureId;
    }

    public abstract float[] getTextCoordinate(int nthImage);

    public abstract void setTextCoordinate(float[] textCoords, int nthPos, int nthPic);

    public int getTextTable(int nthImage) {
        return (int)ceil((double)nthImage/numbeOfImagesPerTexture);
    }

    protected abstract void addImage(BufferedImage newImage) throws IOException;

    private transient LinkedList<DXT1Texture> bufferedImages;



    protected synchronized  void loadTexturesAync() throws IOException {
        ///



        /*IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        ImageSearcher searcher = new GenericFastImageSearcher(Integer.MAX_VALUE,feature);
        ImageSearchHits hits = searcher.search(ir.document(nthImage), ir);*/


        bufferedImages=new LinkedList<>();
        File dir = new File(savePath);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".dxt1");
            }
        });
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int a= parseInt(FilenameUtils.removeExtension(o1.getName()));
                int b= parseInt(FilenameUtils.removeExtension(o2.getName()));
                return a-b;
            }
        });

        DXT1Texture texture=null;
        int emitMessageFrequency=(int)ceil((double)numberOfTextures.get()/100d);
        int i=0;
        for(int j=0;j<files.length;++j){
            FileInputStream fin = new FileInputStream( files[j].getPath());
            ObjectInputStream ois = new ObjectInputStream(fin);

            try {
                texture= (DXT1Texture) ois.readObject();
                ois.close();
                bufferedImages.add(texture);
                if(i%emitMessageFrequency==0){
                    setChanged();
                    notifyObservers(new ProgressMessage((double)i/(double)numberOfTextures.get()));

                }
                ++i;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        numberOfTextures.set(files.length);
        setChanged();
        notifyObservers(new ProgressMessage(1.0d));
        setChanged();

        notifyObservers(new FinishedProcessedCollectionLoading());
    }

    public void calculateGroupsByDate() throws IOException {
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        /*Sort sort=new Sort(new SortedNumericSortField("sortedNum",SortField.Type.LONG,false));
        IndexSearcher is=new IndexSearcher(ir);
        TopDocs topDocs=is.search(new MatchAllDocsQuery(),Integer.MAX_VALUE,sort);
        ScoreDoc[] scoreDoc=topDocs.scoreDocs;*/
        TreeMap<Date,Integer> groupsByDate2=new TreeMap<Date,Integer>();
        Date tempDate=null;
        String tempStringDate;

        for(int i=0;i<numberOfImages;++i){

            tempStringDate=ir.document(i).get("dateRefactored");

            try {
                tempDate=ConvertStringToDate(tempStringDate,"EEE MMM dd HH:mm yyyy");


            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(groupsByDate2.containsKey(tempDate)){
                groupsByDate2.put(tempDate,groupsByDate2.get(tempDate)+1);
            }
            else{
                groupsByDate2.put(tempDate,1);
            }

        }
        System.out.println("---------Groups based on date----");
        this.groupsByDate=new LinkedList<>();

        groupsByDate2.forEach((e,f)->{
            groupsByDate.addLast(f);
            //System.out.println(e+" "+f);
        });



        maxColSize=groupsByDate2.size();
        maxRowSize= Utils.getMaxEntryInMapBasedOnValue(groupsByDate2);



    }

    public void loadTextures() throws IOException {
        //loadTexturesAync();
        orderByTime=new int[numberOfImages];
        orderByTime=getOrderByTime();
        calculateGroupsByDate();


        new Thread(() -> {
            try {
                loadTexturesAync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void reloadTextures() throws IOException {

        new Thread(() -> {
            try {
                reloadTexturesAync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    protected synchronized  void reloadTexturesAync() throws IOException {

        bufferedImages=new LinkedList<>();
        File dir = new File(savePath);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".dxt1") ;
            }
        });


        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                System.out.println("getname================"+o1.getName());
                int a= parseInt(FilenameUtils.removeExtension(o1.getName()));
                int b= parseInt(FilenameUtils.removeExtension(o2.getName()));
                return a-b;
            }
        });


        DXT1Texture texture=null;
        int emitMessageFrequency=(int)ceil((double)numberOfTextures.get()/100d);
        int i=0;
        for(int j=0;j<files.length;++j){
            FileInputStream fin = new FileInputStream( files[j].getPath());
            ObjectInputStream ois = new ObjectInputStream(fin);


            try {

                texture= (DXT1Texture) ois.readObject();
                ois.close();
                bufferedImages.add(texture);
                if(i%emitMessageFrequency==0){
                    setChanged();
                    notifyObservers(new ProgressMessage((double)i/(double)numberOfTextures.get()));

                }
                ++i;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        numberOfTextures.set(files.length);
        setChanged();
        notifyObservers(new ProgressMessage(1.0d));
        setChanged();

        notifyObservers(new FinishedProcessedCollectionLoading());
    }

    public void bindTextures() {

        if(-1!=textureId){
            //glDeleteTextures(textureId);
            //System.out.println("Bind texture torolni kene");
            //GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureId);
        }
        int width = size*numbeOfImagesPerTexture;
        int height = size;
        textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 1, GL_COMPRESSED_RGBA_S3TC_DXT1_EXT, width, height,numberOfTextures.get());

       Iterator<DXT1Texture> it = bufferedImages.iterator();

        int textureIndex=0;
        while (it.hasNext()) {
            DXT1Texture what=it.next();
            byte[] temp=what.getTexture();
            ByteBuffer miert=BufferUtils.createByteBuffer(temp.length);
            miert.put(temp);
            miert.flip();
            glCompressedTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, textureIndex, what.getWidth(), what.getHeight(), 1, GL_COMPRESSED_RGBA_S3TC_DXT1_EXT,miert);
            textureIndex++;
        }
        bufferedImages=null;
        imageList=null;

    }

    private  void initDetection() throws IOException {



        criteria = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(BufferedImage.class, DetectedObjects.class)
                .optFilter("backbone", "resnet50")
                .optProgress(new ProgressBar())
                .build();





            try {
                zooModel = ModelZoo.loadModel(criteria);
                System.out.println("ZooModel loaded");
            } catch (MalformedModelException | ModelNotFoundException e) {
                e.printStackTrace();
            }



        predictor = zooModel.newPredictor();


    }

    private void processDirectoryAsync(String path, LinkedList<Class> features) throws IOException {
        System.out.println("processDirectoryAsync");
        /*        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println(GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");*/
        long startTime = System.currentTimeMillis();
        ConcurrentLinkedQueue<ProcessedImage> imageQueue=new ConcurrentLinkedQueue<>();
        int numberOfThread=Runtime.getRuntime().availableProcessors();
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThread);
        AtomicInteger index=new AtomicInteger(0);
        AtomicInteger erors=new AtomicInteger(0);
        featuresList=features;
        ArrayList<String> images = FileUtils.getAllImages(new File(path), true);


        metadataDates=new HashMap<String,Date>();
        lastModifiedDates=new ArrayList<Date>();
        int emitMessageFrequency=(int)ceil((double)images.size()/100d);
        try {
            Files.walk(Paths.get(savePath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch(Exception ex){
            System.out.println("---------------------------------"+savePath);
            System.out.println(ex);
            System.out.println("----------------------------------");
        }
        new File(savePath).mkdirs();
        ///
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        long windowHandle = glfwCreateWindow(1, 1, "Kristof", NULL, NULL);
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();
        MAX_SIZE=GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        openGLLimit=GL11.glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS);
        glfwDestroyWindow(windowHandle);
        size=min((openGLLimit*MAX_SIZE)/images.size(),maxIconResolution);
        numbeOfImagesPerTexture=(MAX_SIZE)/size;
        System.out.println("MAX_SIZE:"+MAX_SIZE);
        System.out.println("openGLLimit:"+openGLLimit);
        System.out.println("size:"+size);
        System.out.println("numbeOfImagesPerTexture:"+numbeOfImagesPerTexture);
        ///
        maxWidth=size*numbeOfImagesPerTexture;
        image=new BufferedImage(
                maxWidth, size, //work these out
                BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
        numberOfTextures.getAndIncrement();
        imageList.addLast(image);


        IndexWriter iw = LuceneUtils.createIndexWriter(FSDirectory.open(Paths.get(savePath+"\\index")),true,LuceneUtils.AnalyzerType.WhitespaceAnalyzer);
        int imagesSize=images.size();
        Semaphore semaphore = new Semaphore(0);
        for(int i=0;i<numberOfThread;++i){
            executor.submit(() -> {
                GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(false,null, false);
                if (!features.contains(FuzzyOpponentHistogram.class)){
                    globalDocumentBuilder.addExtractor(FuzzyOpponentHistogram.class);
                }
                for(Class it:features) {
                    if(!it.getSimpleName().equals("LastModificationDate") && !it.getSimpleName().equals("ObjectDetection")){
                        globalDocumentBuilder.addExtractor(it);}
                    if(it.getSimpleName().equals("ObjectDetection")){
                        objectDetectionSwitch=true;

                    }
                }

                BufferedImage bi;
                BufferedImage scaledImg;
                int tempIndex;
                String imagePath;
                Document document;


               // Date dd;
                while(true){
                    tempIndex=index.getAndIncrement();
                    if(tempIndex>=imagesSize){
                        break;
                    }
                    try {

                        imagePath=images.get(tempIndex);
                        bi=ImageIO.read(new File(imagePath));
                        scaledImg = Scalr.resize(bi, Scalr.Mode.FIT_EXACT, size, size);
                        document = globalDocumentBuilder.createDocument(bi,imagePath);
                        //document.add(new StoredField("lastModified",dd.toString()));


                        imageQueue.add(new ProcessedImage(scaledImg,document));
                        semaphore.release();
                    }
                    catch(Exception ex){
                        erors.getAndIncrement();
                        System.out.println("----------------------------------");
                        System.out.println("TESZT:"+ex);
                        System.out.println("----------------------------------");
                    }


                }
                latch.countDown();
                semaphore.release();

                return null;
            });

        }
        int counter=0;
        saveTextureExecutor= (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThread);
        phaser=new Phaser();
        Date lmd; // as Last Modified Date
        String imageIdentifier;
        while(true){
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ProcessedImage result=imageQueue.poll();
            if(null==result){
                if(latch.getCount()==0) {
                    break;
                }
                else
                {
                    continue;
                }
            }
            try {

                imageIdentifier=result.document.get("ImageIdentifier");

                if(objectDetectionSwitch==true){
                    DetectedObjects detection=predictor.predict(result.image);
                    HashSet<String> detectedObjects=new HashSet<>();
                    if(detection != null){
                        if(detection.getNumberOfObjects()>0){
                            System.out.println(detection);
                            detection.items().forEach((f)->{
                              if(f.getProbability()>0.7){
                                  detectedObjects.add(f.getClassName());
                              }
                            });

                        }
                    }
                    org.bson.Document doc=new org.bson.Document("image",imageIdentifier).append("labels",detectedObjects);
                    System.out.println(doc);
                    DB.insertImg(doc);
                    if(!detectedObjects.isEmpty()){
                        DB.insertLabel(doc);
                    }
                    //DB.insertLabel(doc);
                }



                //EEE MMM dd HH:mm:ss XXX yyyy




                lmd= GetDateMetadataFromPath(imageIdentifier);

                DateFormat sdf=new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.getDefault());
                //Date datum=sdf.parse(lmd.toString());
                //System.out.println("-------->>>> "+sdf.parse(lmd.toString()));
                //sortedNum-double
                result.document.add(new NumericDocValuesField("sortedNum",lmd.getTime()));

                String newDate=sdf.format(lmd);

                result.document.add(new StoredField("dateRefactored", newDate));
                iw.addDocument(result.document);
                addImage(result.image);




                if(counter%emitMessageFrequency==0){
                    setChanged();
                    notifyObservers(new ProgressMessage((double)counter/(double)(imagesSize-erors.get())));
                }
                counter++;
            }
            catch(Exception ex){
                System.out.println("----------------------------------");
                System.out.println("ProcessDirAsync:"+ex);
                System.out.println("----------------------------------");
            }


        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        executor.shutdown();
        iw.close();
        if(0==numberOfImages){
            setChanged();
            notifyObservers(new NoImagesFound());
            return;
        }
        System.out.println("FELDOLDOZAS UTAN A KEPEK SZAMA="+numberOfImages);

        saveTexture();
        phaser.register();
        phaser.arriveAndAwaitAdvance();
        saveTextureExecutor.shutdown();
        System.out.println("*************");
        System.out.println("Final number of textures:"+numberOfTextures);
        System.out.println("*************");
        setChanged();

        notifyObservers(new ProgressMessage(1.0d));
        setChanged();
        notifyObservers(new FinishedCollectionProcessingMessage());

    }

    public void processDirectory(String path, LinkedList<Class> features) throws IOException {
        objectDetectionSwitch=false;
        if (features.contains(ObjectDetection.class)) {
            objectDetectionSwitch=true;
            String dbname=savePath;
            dbname=dbname.replace("\\","_");
            initDetection();
            DB=new MongoApi(dbname);
        }


        new Thread(() -> {
            try {
                processDirectoryAsync(path,features);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    transient ThreadPoolExecutor saveTextureExecutor;
    transient Phaser phaser;

    protected void saveTexture() throws IOException {
/*        File outputfile = new File(savePath+"\\"+(numberOfTextures-1)+".png");
        ImageIO.write(image, "png", outputfile);*/


        Semaphore semaphore = new Semaphore(0);
        saveTextureExecutor.submit(() -> {
            int textureId=numberOfTextures.getAndIncrement();
            BufferedImage temp=image;
            phaser.register();
            semaphore.release();
            int width=temp.getWidth();
            int height=temp.getHeight();
            int[] pixels = new int[width * height];
            temp.getRGB(0, 0, width, height, pixels, 0, width);
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4 );
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    /* Pixel as RGBA: 0xAARRGGBB */
                    int pixel = pixels[y * width + x];
                    /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                    buffer.put((byte) (pixel & 0xFF));
                    /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            buffer.flip();

            byte[] b = new byte[buffer.remaining()];
            buffer.get(b);
            byte[] result=Squish.compressImage(b,width,height,null,DXT1);
            FileOutputStream fos = new FileOutputStream(savePath+"\\"+(textureId-1)+".dxt1");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new DXT1Texture(temp.getWidth(),temp.getHeight(),result));
            oos.close();
            phaser.arriveAndDeregister();
            return null;
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        image=new BufferedImage(
                maxWidth, size, //work these out
                BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
        xImage=0;

/*        try {
            Path path = Paths.get(savePath+"\\"+(numberOfTextures-1)+".dxt1");
            Files.write(path, result);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        ////
    }

    public double[] getFloatingScores() throws IOException {
        BufferedImage img = new BufferedImage(256, 256,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,256,256);
        g.dispose();
        //img.getGraphics().setColor(Color.WHITE);
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        ImageSearcher searcher = new GenericFastImageSearcher(Integer.MAX_VALUE,FuzzyOpponentHistogram.class);

        ImageSearchHits hits = searcher.search(img, ir);
        //ImageSearchHits hits = searcher.search(bufferedImage, ir);
        double[] scores=new double[numberOfImages];

        for(int i=0;i<numberOfImages;++i){
            scores[hits.documentID(i)]=hits.score(i);

            // System.out.println(scores[i]);


        }


        return scores;
    }

    public HashMap<Integer,Integer> getFloatingGroups(String format) throws IOException {
        HashMap<Integer,Integer> floatingGroupsByDate=new HashMap<Integer,Integer>();
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        /*Sort sort=new Sort(new SortedNumericSortField("sortedNum",SortField.Type.LONG,false));
        IndexSearcher is=new IndexSearcher(ir);
        TopDocs topDocs=is.search(new MatchAllDocsQuery(),Integer.MAX_VALUE,sort);
        ScoreDoc[] scoreDoc=topDocs.scoreDocs;*/
            TreeMap<Date,ArrayList<Integer>> groupsByDate2= new TreeMap<>();
            Date tempDate=null;
            String tempStringDate;

            for(int i=0;i<numberOfImages;++i){

                tempStringDate=ir.document(i).get("dateRefactored");
                ///System.out.println(ir.document(i).get("ImageIdentifier")+"   "+tempStringDate);
                try {
                    tempDate=ConvertStringToDate(tempStringDate,format);//"EEE MMM dd HH:mm yyyy"


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(groupsByDate2.containsKey(tempDate)){
                    groupsByDate2.get(tempDate).add(i);
                }
                else{
                    groupsByDate2.put(tempDate, new ArrayList<>());
                    groupsByDate2.get(tempDate).add(i);
                }

            }

            int col=-1;

        for (Map.Entry<Date, ArrayList<Integer>> entry : groupsByDate2.entrySet()) {

            ArrayList<Integer> f = entry.getValue();
            col++;

            for (int i = 0; i < f.size(); ++i) {
                floatingGroupsByDate.put(f.get(i),col);
                //System.out.println(e+" --- "+f.get(i)+" --- "+col);
            }

        }
        return floatingGroupsByDate;

    }

    public double[][] getDistanceBasedPositions(){

             return   distanceBasedPositions;
    }

    public void calculateDistanceMatrix(Class feature){
        new Thread(() -> {

            try {
                double[][] d2=getDistances(feature);

                distanceBasedPositions=tSNE.getDistanceBasedPositions(d2);

                System.out.println("Distance based posi done");
//                  System.out.println("distanceBasedPositions "+distanceBasedPositions.length+" ");



            } catch (IOException e) {
                e.printStackTrace();
            }



        }).start();
    }

    public double[][] getDistances(Class feature) throws IOException {
        System.out.println("Distances calculation began");
        StopWatch stopwatch=new StopWatch();
        stopwatch.start();
        double[][] distances=new double[numberOfImages][numberOfImages];
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        //ImageSearcher searcher = new GenericFastImageSearcher(Integer.MAX_VALUE, feature);
        ImageSearcher searcher = new GenericFastImageSearcher(Integer.MAX_VALUE, feature);



        for(int i=0;i<numberOfImages;++i){
            ImageSearchHits hits = searcher.search(ir.document(i), ir);

            for(int j=0;j<numberOfImages;++j){
                distances[i][hits.documentID(j)]=hits.score(j);
            }
        }


        /*for(int i=0;i<numberOfImages;++i){

            for(int j=0;j<numberOfImages;++j){
                System.out.print(distances[i][j]+"  ");
            }
            System.out.println();
        }*/
        stopwatch.stop();    // Optional
        long timeElapsed = stopwatch.getTime();
        System.out.println("---Distance time: " + timeElapsed/1000);
        System.out.println("Distances calculated");
        return distances;}

    public int[] getOrder(int nthImage,Class feature) throws IOException {
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        ImageSearcher searcher = new GenericFastImageSearcher(Integer.MAX_VALUE,feature);
        ImageSearchHits hits = searcher.search(ir.document(nthImage), ir);
        //ImageSearchHits hits = searcher.search(bufferedImage, ir);
        int[] order=new int[numberOfImages];

        for(int i=0;i<numberOfImages;++i){
            order[hits.documentID(i)]=i;

            //System.out.println(hits.score(i));


        }


        return order;
    }

    @Override
    public double[] getScores(int nthImage, Class feature) throws IOException {
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        ImageSearcher searcher = new GenericFastImageSearcher(Integer.MAX_VALUE,feature);
        ImageSearchHits hits = searcher.search(ir.document(nthImage), ir);
        //ImageSearchHits hits = searcher.search(bufferedImage, ir);
        double[] scores=new double[numberOfImages];

        for(int i=0;i<numberOfImages;++i){
            scores[i]=hits.score(i);

           // System.out.println(scores[i]);


        }


        return scores;
    }

    public int[] getOrderByTime() throws IOException {
        orderByTime=new int[numberOfImages];
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));
        Sort sort=new Sort(new SortedNumericSortField("sortedNum",SortField.Type.LONG,false));
        IndexSearcher is=new IndexSearcher(ir);
        TopDocs topDocs=is.search(new MatchAllDocsQuery(),Integer.MAX_VALUE,sort);
        ScoreDoc[] scoreDoc=topDocs.scoreDocs;
        for(int i=0;i<numberOfImages;++i){
            orderByTime[scoreDoc[i].doc]=i;

            //System.out.println(ir.document(scoreDoc[i].doc).get("ImageIdentifier")+"   "+ir.document(scoreDoc[i].doc).get("dateRefactored"));
        }
        return orderByTime;
    }

    public synchronized void clean() {
        if(-1!=textureId){
            glDeleteTextures(textureId);
            textureId=-1;
        }
        //imageList=null;
    }

    public String getSavePath() {
        return savePath;
    }

    private void saveSelectedImagesToDirectoryAsync(String savePath, TreeSet<Integer>  selectedImages){
        int numberOfThread=Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThread);
        //ConcurrentLinkedQueue<Integer> queue=new ConcurrentLinkedQueue<>(selectedImages);
        Iterator<Integer> it = selectedImages.iterator();
        int potentionalImageNumber=selectedImages.size();
        int emitMessageFrequency=(int)ceil((double)potentionalImageNumber/100d);
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        for(int i=0;i<numberOfThread;++i){
            executor.submit(() -> {
                IndexReader reader =DirectoryReader.open(FSDirectory.open(Paths.get(this.savePath+"\\index"))); // create IndexReader
                StringBuilder sourceBuilder = new StringBuilder();
                StringBuilder destBuilder= new StringBuilder();
                destBuilder.append(savePath);
                destBuilder.append("\\");
                //Integer index=0;
                int index=0;
                while(true){
                    synchronized (it){
                        if(it.hasNext()){
                            index=it.next();
                        }
                        else{
                            break;
                        }
                    }
                    try {
                        sourceBuilder.append(reader.document(index).get("ImageIdentifier"));
                        Path p = Paths.get(sourceBuilder.toString());
                        String file = p.getFileName().toString();
                        destBuilder.append(file);
                        Files.copy(p, Paths.get(destBuilder.toString()), StandardCopyOption.REPLACE_EXISTING);
                        if(index%emitMessageFrequency==0){
                            setChanged();
                            notifyObservers(new ProgressMessage((double)index/(double)potentionalImageNumber));

                        }
                    }
                    catch(Exception ex){
                    }
                    finally {
                        sourceBuilder.setLength(0);
                        destBuilder.setLength(savePath.length()+1);
                    }

                }

                reader.close();
                latch.countDown();
                return null;
            });

        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        setChanged();
        notifyObservers(new VirtualDirectoryCollectionSavingToFolderCompleted());
    }

    public void saveSelectedImagesToDirectory(String savePath, TreeSet<Integer>  selectedImages) throws IOException {
        new Thread(() -> {
            saveSelectedImagesToDirectoryAsync(savePath,selectedImages);
        }).start();
    }

    public  TreeSet<Integer> getImagesByLabel(HashSet<String> strings) throws IOException, org.apache.lucene.queryparser.classic.ParseException {

        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(savePath+"\\index")));


        ArrayList<Term> terms=new ArrayList<>();
        System.out.println("Identifiers to find= "+strings.size());
        strings.forEach((s)->{
            terms.add(new Term("ImageIdentifier",s));
        });
        Query query=new TermsQuery(terms);

        IndexSearcher is=new IndexSearcher(ir);

        //TopDocs topDocs=is.search(new MatchAllDocsQuery(),Integer.MAX_VALUE,sort);
        TopDocs topDocs=is.search(query,this.numberOfImages);
        ScoreDoc[] hits=topDocs.scoreDocs;


        TreeSet<Integer> vegso=new TreeSet<>();

        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            vegso.add(docId);
        }
        ir.close();
        return vegso;
    }
}
