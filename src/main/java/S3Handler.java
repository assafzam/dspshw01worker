import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
import java.util.List;
import java.util.UUID;


public class S3Handler {

    private AmazonS3 s3;
    private AWSCredentialsProvider credentialsProvider;


    S3Handler() {
        this.credentialsProvider = new AWSStaticCredentialsProvider(new ProfileCredentialsProvider().getCredentials());
        this.s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-1")
                .build();

    }

    public List <Bucket> getBucketlist() {
        return  s3.listBuckets();
    }
    public void deleteBucket(String bucketName){
        s3.deleteBucket(bucketName);
    }
    public void deleteObject(String bucketName,String objectName){
        s3.deleteObject(bucketName, objectName);
    }
    S3Object downloadObjectFromS3(String bucketName, String fileName) {

        S3Object s3Object = null;
        try {

            System.out.println("Downloading an object");
            s3Object = s3.getObject(new GetObjectRequest(bucketName, fileName));
            if (s3Object == null) {
                throw new Exception("Object not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s3Object;
    }

//    String getContent(String bucketName, String summaryDir) {
//        S3Object s3Object = downloadObjectFromS3(bucketName, summaryDir);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
//        String content = "";
//
//        while (true) {
//            String line = null;
//            try {
//                line = reader.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (line == null)
//                break;
//
//            content = content.concat(line);
//        }
//        return content;
//    }

    String createBucket(String bucketNamePrefix){
        String bucketName =
                ((credentialsProvider.getCredentials().getAWSAccessKeyId()
                        + "-" + bucketNamePrefix
                        + "-" + UUID.randomUUID())
                        .replace('\\', '-').replace('/', '-').replace(':', '-')).toLowerCase();
        bucketName = bucketName.substring(0,62);
        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");
        try {

            System.out.println("Creating bucket " + bucketName + "\n");
            s3.createBucket(bucketName);
            // Upload a text string as a new object.
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        return bucketName;

    }
    void uploadObjectsFromDir(String bucketName, String path){

        File dir = new File(path);
        String key;
        for (File file : dir.listFiles()){
            key = file.getName().replace('\\', '_').replace('/', '_').replace(':', '_');
            PutObjectRequest req = new PutObjectRequest(bucketName, key, file);
            s3.putObject(req);
        }
    }



    void uploadObject(String bucketName, String filePath){
        System.out.println("Uploading a new object to S3 from a file\n");

        String key = filePath.replace('\\', '_').replace('/', '_').replace(':', '_');
        File file = new File(filePath);
        PutObjectRequest req = new PutObjectRequest(bucketName, key, file);
        s3.putObject(req);
    }



    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

}
