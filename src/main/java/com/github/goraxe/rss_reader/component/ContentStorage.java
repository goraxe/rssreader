package com.github.goraxe.rss_reader.component;

import com.github.goraxe.rss_reader.ContentNotFound;
import com.github.goraxe.rss_reader.domain.Content;
import com.google.common.io.Files;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by goraxe on 2016-04-17.
 * Boo Yar
 */
@Component
public class ContentStorage {

    private String repoDir;
    static private final Logger log = LoggerFactory.getLogger(URLFetcher.class);

    ContentStorage() {
        repoDir = "results/";
    }


    public void setRepoDir(String repoDir) {
        this.repoDir = repoDir;
    }


    private File getResultsDir(String digest) {
        String resultsDirPath = repoDir + digest.substring(0,2);
        File resultsDir = new File(resultsDirPath);

        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }
        return resultsDir;
    }

    public Content saveContent(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        Content content = new Content();

        File temp = File.createTempFile("temp-download", ".tmp");
        String digest = saveTempFileWithDigest(inputStream, temp, content);

        content.setId(digest);
        File resultsDir = getResultsDir(digest);

        String path = resultsDir + "/" + digest;
        File stored = new File(path);
        log.info("saving to temp file: " + stored.getAbsolutePath());
        Files.move(temp, stored);

        return content;
    }

    private String saveTempFileWithDigest(InputStream is, File temp, Content content) throws IOException, NoSuchAlgorithmException {
        int                 nread       = 0;
        byte[]              dataBytes   = new byte[1024];

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileOutputStream fo = new FileOutputStream(temp);
            log.trace("storing content to temp file: " + temp.getAbsolutePath());
            StringBuilder sb = new StringBuilder();
            while ((nread = is.read(dataBytes)) != -1) {
                fo.write(dataBytes);
                md.update(dataBytes, 0, nread);
                if (nread < dataBytes.length) {
                    byte[] copy = new byte[nread];
                    for (int i = 0; i < nread; i++) {
                        copy[i] = dataBytes[i];

                    }
                    sb.append(new String(copy));
                } else {
                    sb.append(new String(dataBytes));
                }
            }
            content.setContent(sb.toString());
            fo.close();

            return getDigestHexString(md);
        }
        catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm is missing");
            throw e;
        }
    }

    private String getDigestHexString(MessageDigest md) {
        StringBuffer        sb          = new StringBuffer();
        byte[] mdbytes = md.digest();
        for (int i =0; i < mdbytes.length; i++ ) {
            sb.append(Integer.toHexString(0xFF & mdbytes[i]));
        }
        return sb.toString();
    }

    public Content getContent(String id) throws ContentNotFound, IOException {
        File resultsDir = getResultsDir(id);

        String path = resultsDir + "/" + id;
        File stored = new File(path);

        if(stored.exists()) {
            Content result = new Content();
            result.setId(id);

            BufferedInputStream is = new BufferedInputStream(new FileInputStream(stored));
            int nread = 0;
            StringBuilder sb = new StringBuilder();
            byte[] b = new byte[1024];
            while ((is.read(b)) != -1) {
                sb.append(b);
            }
            result.setContent(sb.toString());
            return result;

        }
        else {
            throw new ContentNotFound();
        }
    }

    public boolean deleteContent(String id) {
        if (exists(id)) {
            File resultsDir = getResultsDir(id);

            String path = resultsDir + "/" + id;
            File stored = new File(path);

            return stored.delete();
        }
        return false;
    }

    public boolean exists(String id) {
        File resultsDir = getResultsDir(id);

        String path = resultsDir + "/" + id;
        File stored = new File(path);
        return stored.exists();
    }
}
