import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class Monitor {

    static String[] warnWords = {"[WARN]", "warning", "WAR", "<war>"};
    static String[] infoWords = {"[INFO]", "info", "INFO", "information", "<info>"};
    static String[] errorWords = {"[ERROR]", "error:", "Error", "*ERROR*", "failed", "failure"};

    public static void main(String[] args) {
        new Monitor().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        SparkConf conf = new SparkConf().setAppName(Monitor.class.getName());
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> logs = sc.textFile(inputFilePath);
        JavaPairRDD<String, Integer> statistics = logs.mapToPair(message -> {
                    String[] words = message.split(" ");
                    int numberOfWarnings= 0;
                    int numberOfInfos= 0;
                    int numberOfErrors= 0;
                    for (String word: words) {
                        if (ArrayUtils.contains(warnWords, word)) {
                            numberOfWarnings++;
                        } else if (ArrayUtils.contains(errorWords, word)) {
                            numberOfErrors++;
                        } else {
                            numberOfInfos++;
                        }
                    }
                    int max = Math.max(Math.max(numberOfErrors, numberOfInfos), numberOfWarnings);
                    if(numberOfErrors==max) return new Tuple2<>("ERROR", 1);
                    else if(numberOfInfos==max) return new Tuple2<>("INFO", 1);
                    else return new Tuple2<>("WARN", 1);
        }).reduceByKey(Integer::sum);

        statistics.saveAsTextFile(outputDir);
    }
}
