package DB;


import com.mongodb.Block;
import com.mongodb.QueryBuilder;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.lwjgl.system.CallbackI;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.push;

public class MongoApi {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    transient  private static MongoCollection<Document> labelCenteredColl;
    transient private static MongoCollection<Document> imageCenteredColl;



    public MongoApi(String dbname){
        try{
            mongoClient = MongoClients.create("mongodb://localhost");
            database=mongoClient.getDatabase(dbname);

        }catch (Exception e){
            e.fillInStackTrace();
        }



    }


    public HashSet<String> getIdentifiersForLabelsFromDb(ArrayList<String> labelList){
        labelCenteredColl=database.getCollection("LabelCenteredColl");
       //labelCenteredColl.createIndex(Indexes.text("label"), (result, t) -> { System.out.println("CreateIndex: "+result);});

        CompletableFuture<HashSet<String>> resultList=new CompletableFuture<>();
        HashSet<String> imgs=new HashSet<>();
        labelCenteredColl.find(Filters.in("label",labelList)).forEach(document -> {
            try {
                imgs.addAll(document.getList("images",String.class));
            } catch (Exception e) {
                System.out.println(e);
            }
        },(final Void result, final Throwable t) -> {
            resultList.complete(imgs);
        });


        HashSet<String> result=new HashSet<>();
        try {
            result=resultList.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public static List<String> getDetectedObjects(String DbName) throws  InterruptedException {
        database=mongoClient.getDatabase(DbName);
        labelCenteredColl=database.getCollection("LabelCenteredColl");

        CompletableFuture<List<String>> future = new CompletableFuture<>();
        ArrayList<String> list = new ArrayList<>();
        labelCenteredColl.find().forEach((document) -> {
            try {
                list.add(document.getString("label"));
            } catch (Exception e) {
                System.out.println(e);
            }

        }, (final Void result, final Throwable t) -> {
            future.complete(list);
        });

        List<String> resultList = null;
        try {
            resultList = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultList;




    }


    public  void insertImg(Document doc){
        imageCenteredColl=database.getCollection("ImageCenteredColl");
        imageCenteredColl.insertOne(doc, (result1, t) -> System.out.println("Inserted!"));



    }

    public void  insertLabel(Document doc){
        UpdateOptions updateOption = new UpdateOptions().upsert(true);
        Bson updateOperation = addToSet("images" ,doc.get("image"));
        labelCenteredColl=database.getCollection("LabelCenteredColl");
        HashSet<String> labels= (HashSet<String>) doc.get("labels");
        labels.forEach((label)->{
            Bson filter=eq("label",label);
            labelCenteredColl.updateOne(filter, updateOperation, updateOption, new SingleResultCallback<UpdateResult>() {
                @Override
                public void onResult(UpdateResult result, Throwable t) {
                    System.out.println("Updated "+result.getModifiedCount());
                }
            });


        });
    }



}
