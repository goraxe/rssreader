package com.github.goraxe.rss_reader.component;

import com.github.goraxe.rss_reader.ContentNotFound;
import com.github.goraxe.rss_reader.domain.Content;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by goraxe on 2016-04-17.
 * Boo Yar
 */
public class ContentStorageTest {

    ContentStorage test;

    @Rule
    public TemporaryFolder folder;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        folder = new TemporaryFolder();
        folder.create();
        test = new ContentStorage();

        test.setRepoDir(folder.getRoot().getPath());
    }

    @After
    public void tearDown() throws Exception {
        folder.delete();
    }

    @Test
    public void saveContent() throws IOException, NoSuchAlgorithmException {
        String testData = "this is test data";
        ByteArrayInputStream content = new ByteArrayInputStream(testData.getBytes());

        Content resultA = test.saveContent(content);
        assertNotNull(resultA);
        assertEquals("this is test data", resultA.getContent());

        Content resultB = null;
        try {
            resultB = test.getContent(resultA.getId());
        } catch (ContentNotFound contentNotFound) {
            fail("Content not found exception during save");
        } catch (IOException e) {
            fail("IO Exception thrown");
        }
        assertNotNull(resultB);

        assertEquals(resultA.getContent(), "this is test data");
    }

    @Test
    public void saveContentLarge() throws IOException, NoSuchAlgorithmException {
        String testData = "this is test data";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(testData);
        }
        testData = sb.toString();
        ByteArrayInputStream content = new ByteArrayInputStream(testData.getBytes());

        Content resultA = test.saveContent(content);
        assertNotNull(resultA);
        assertEquals(testData, resultA.getContent());

    }

    @Test()
    public void getContentDoesNotExist() throws IOException, ContentNotFound {
        thrown.expect(ContentNotFound.class);
        Content resultA = test.getContent("DoesNotExist");
    }

    @Test
    public void deleteContent() throws IOException, NoSuchAlgorithmException {
        String testData = "this is test data";
        ByteArrayInputStream content = new ByteArrayInputStream(testData.getBytes());

        Content resultA = test.saveContent(content);
        assertNotNull(resultA);
        assertEquals("this is test data", resultA.getContent());

        assertTrue(test.deleteContent(resultA.getId()));
        assertFalse(test.exists(resultA.getId()));

        assertFalse(test.deleteContent(resultA.getId()));
    }
}