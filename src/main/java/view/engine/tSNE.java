package view.engine;

import javafx.scene.paint.Stop;
import model.Collections.Collection;
import org.apache.commons.lang3.time.StopWatch;
import org.deeplearning4j.clustering.algorithm.Distance;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.guava.base.Stopwatch;
import smile.manifold.LLE;
import smile.manifold.TSNE;
import smile.manifold.UMAP;
import smile.math.matrix.Matrix;
import smile.mds.MDS;
import smile.mds.SammonMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

public final class tSNE {



    public static double normalize(double min , double max, double value){

        double n=2*((value-min)/(max-min))-1;
        return n;
    }

    public static double[][] normalizeAll(INDArray d){
        double[][] positions=new double[d.rows()][2];

        double maxi= d.maxNumber().doubleValue();
        double mini= d.minNumber().doubleValue();
        double[][] valami =d.toDoubleMatrix();
        for(int i=0;i<d.rows();i++){
            positions[i][0]=normalize(mini,maxi,valami[i][0]);
            positions[i][1]=normalize(mini,maxi,valami[i][1]);

        }
        return positions;
    }


    private static INDArray calculate(double[][] distances){


        //30
        /*  10
            50
            100
            250
            500
            750
            1000
        */
        int iter=10;
        int learningRate=500;
        double theta=0.5;
        double perplexity=30;

        StopWatch stopwatch=new StopWatch();
        stopwatch.start();
        BarnesHutTsne tsne = new BarnesHutTsne.Builder()
                .setMaxIter(iter)
                //.learningRate(learningRate)
                .useAdaGrad(false)
                .theta(theta)
                .perplexity(perplexity)
                //.setMomentum(0.5)
                .normalize(true)
                .build();

        tsne.fit(Nd4j.create(distances));
        stopwatch.stop();


        long timeElapsed = stopwatch.getTime();

        System.out.println("----Execution time in seconds: " + timeElapsed/1000);

        return tsne.getData();
    }

    public static double[][] getDistanceBasedPositions(double[][] d){
        double[][] newD=normalizeAll(calculate(d));
        return newD;

    }
}
